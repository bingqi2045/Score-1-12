package org.oagi.srt.cache;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class DatabaseCacheWatchdog<T>
        implements InitializingBean, DisposableBean, Runnable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private RedissonClient redissonClient;

    private final String tableName;
    private final Class<T> mappedClass;
    private final RedisCacheConfiguration cacheConfig;

    private String camelCasePriKeyName;
    private String underscorePriKeyName;

    private List<String> tableFields;

    private long delay = 30L;
    private TimeUnit unit = TimeUnit.SECONDS;

    private ScheduledExecutorService scheduledExecutorService;

    public DatabaseCacheWatchdog(String tableName, Class<T> mappedClass,
                                 RedisCacheConfiguration redisCacheConfiguration) {
        this.tableName = tableName;
        this.mappedClass = mappedClass;
        this.cacheConfig = redisCacheConfiguration;

        setPrimaryKeyName(this.tableName + "_id");
    }

    public void setDelay(long delay, TimeUnit unit) {
        this.delay = delay;
        this.unit = unit;
    }

    public void setPrimaryKeyName(String primaryKeyName) {
        this.underscorePriKeyName = primaryKeyName;
        this.camelCasePriKeyName = underscoreToCamelCase(primaryKeyName);
    }

    public String underscoreToCamelCase(String string) {
        String str = Arrays.asList(string.split("_")).stream()
                .map(e -> Character.toUpperCase(e.charAt(0)) + e.substring(1).toLowerCase())
                .collect(Collectors.joining(""));
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public String getTableName() {
        return this.tableName;
    }

    @Override
    public void afterPropertiesSet() {
        tableFields = loadFields(this.tableName);

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(this, 0L, delay, unit);
    }

    @Override
    public void destroy() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
    }

    private List<String> loadFields(String tableName) {
        List<String> fields = new ArrayList();
        jdbcTemplate.query("DESCRIBE " + tableName, rch -> {
            String field = rch.getString("Field");
            fields.add(field);
        });
        return fields;
    }

    @Override
    public void run() {
        if (logger.isTraceEnabled()) {
            logger.trace("Database/Cache Watchdog for `" + tableName + "` table: investigating...");
        }

        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(this.tableName);
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        try {
            Map<Long, String> checksumFromDatabase = getChecksumFromDatabase();
            List<Long> invalidPrimaryKeys;

            readWriteLock.readLock().lock();
            try {
                Map<Long, String> checksumFromRedis = getChecksumFromRedis(redisConnection);
                if (logger.isDebugEnabled()) {
                    logger.debug("Database/Cache Watchdog for `" + tableName + "` table: retrieved " + checksumFromDatabase.size() + " checksum items.");
                }

                invalidPrimaryKeys = getInvalidPrimaryKeys(checksumFromRedis, checksumFromDatabase);
                if (invalidPrimaryKeys.isEmpty()) {
                    logger.info("Database/Cache Watchdog for `" + tableName + "` table: all data are valid.");
                    return;
                }
            } finally {
                readWriteLock.readLock().unlock();
            }

            readWriteLock.writeLock().lock();
            try {
                logger.info("Database/Cache Watchdog for `" + tableName + "` table: " + invalidPrimaryKeys.size() + " invalid items found.");

                updateChecksumAndData(redisConnection, invalidPrimaryKeys, checksumFromDatabase);
                logger.info("Database/Cache Watchdog for `" + tableName + "` table: successfully updated.");
            } finally {
                readWriteLock.writeLock().unlock();
            }
        } finally {
            redisConnection.close();
        }


        RLock lock = redissonClient.getLock(this.tableName);
        lock.lock();
        try {
            execute();
        } finally {
            lock.unlock();
        }
    }

    private void execute() {

    }

    private Map<Long, String> getChecksumFromRedis(RedisConnection redisConnection) {
        byte[] checksumKey = serializeCacheKey(this.tableName + ":checksum");
        Map<byte[], byte[]> hGetAll = redisConnection.hGetAll(checksumKey);
        if (hGetAll == null) {
            hGetAll = Collections.emptyMap();
        }

        return hGetAll.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Long.parseLong(new String(e.getKey())),
                        e -> new String(e.getValue())));
    }

    private Map<Long, String> getChecksumFromDatabase() {
        Map<Long, String> checksumMap = new HashMap();
        String checksumQuery = getChecksumQuery();
        jdbcTemplate.query(checksumQuery, rch -> {
            checksumMap.put(rch.getLong(this.underscorePriKeyName), rch.getString("checksum"));
        });
        return checksumMap;
    }

    private List<Long> getInvalidPrimaryKeys(
            Map<Long, String> checksumFromRedis,
            Map<Long, String> checksumFromDatabase
    ) {
        List<Long> invalidPrimaryKeys = new ArrayList();

        for (Map.Entry<Long, String> entry : checksumFromDatabase.entrySet()) {
            long priKey = entry.getKey();
            String checksum = entry.getValue();
            if (checksum.equals(checksumFromRedis.get(priKey))) {
                continue;
            }

            invalidPrimaryKeys.add(priKey);
        }

        return invalidPrimaryKeys;
    }

    private void updateChecksumAndData(RedisConnection redisConnection,
                                       List<Long> invalidPrimaryKeys,
                                       Map<Long, String> checksumFromDatabase) {
        Map<Long, T> objectFromDatabase = getObjectFromDatabase();
        if (logger.isTraceEnabled()) {
            logger.trace("Database/Cache Watchdog for `" + tableName + "` table: retrieved object data.");
        }

        Map<byte[], byte[]> invalidChecksumMap = new HashMap();
        Map<byte[], byte[]> invalidObjectMap = new HashMap();
        for (Long invalidPrimaryKey : invalidPrimaryKeys) {
            byte[] field = serializeCacheKey("" + invalidPrimaryKey);

            byte[] checksumValue = serializeCacheValue(checksumFromDatabase.get(invalidPrimaryKey));
            byte[] objectValue = serializeCacheValue(objectFromDatabase.get(invalidPrimaryKey));

            invalidChecksumMap.put(field, checksumValue);
            invalidObjectMap.put(field, objectValue);
        }

        byte[] checksumKey = serializeCacheKey(this.tableName + ":checksum");
        redisConnection.hMSet(checksumKey, invalidChecksumMap);

        byte[] objectKey = serializeCacheKey(this.tableName);
        redisConnection.hMSet(objectKey, invalidObjectMap);
    }

    private String getChecksumQuery() {
        return "SELECT " + this.underscorePriKeyName + ", sha1(concat_ws(`" +
                String.join("`,`", this.tableFields) + "`)) `checksum` FROM " + this.tableName;
    }

    protected byte[] serializeCacheKey(String cacheKey) {
        return ByteUtils.getBytes(cacheConfig.getKeySerializationPair().write(cacheKey));
    }

    protected byte[] serializeCacheValue(Object value) {
        return ByteUtils.getBytes(cacheConfig.getValueSerializationPair().write(value));
    }

    private Map<Long, T> getObjectFromDatabase() {
        return findAll().stream()
                .collect(Collectors.toMap(obj -> {
                    PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(mappedClass, camelCasePriKeyName);
                    try {
                        Long priKey = (Long) pd.getReadMethod().invoke(obj, new Object[]{});
                        return priKey;
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("'" + camelCasePriKeyName + "' method should be accessible.", e);
                    } catch (InvocationTargetException e) {
                        throw new IllegalStateException("'" + camelCasePriKeyName + "' method cannot access.", e.getCause());
                    }
                }, Function.identity()));
    }

    abstract protected List<T> findAll();
}
