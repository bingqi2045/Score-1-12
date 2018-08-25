package org.oagi.srt.cache;

import org.oagi.srt.repository.SrtRepository;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class DatabaseCacheWatchdog<T> extends DatabaseCacheHandler
        implements InitializingBean, DisposableBean, Runnable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private CacheSerializer serializer;

    @Autowired
    private RedissonClient redissonClient;

    private SrtRepository<T> delegate;

    private long delay = 30L;
    private TimeUnit unit = TimeUnit.SECONDS;

    private ScheduledExecutorService scheduledExecutorService;

    public DatabaseCacheWatchdog(String tableName, Class<T> mappedClass,
                                 SrtRepository<T> delegate) {
        super(tableName, mappedClass);
        this.delegate = delegate;
    }

    public void setDelay(long delay, TimeUnit unit) {
        this.delay = delay;
        this.unit = unit;
    }

    public String underscoreToCamelCase(String string) {
        String str = Arrays.asList(string.split("_")).stream()
                .map(e -> Character.toUpperCase(e.charAt(0)) + e.substring(1).toLowerCase())
                .collect(Collectors.joining(""));
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(this, 0L, delay, unit);
    }

    @Override
    public void destroy() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (Throwable t) {
            logger.error("Database/Cache Watchdog for `" + getTableName() + "` table: get caught an exception.", t);
        }
    }

    private void execute() {
        String tableName = getTableName();
        if (logger.isTraceEnabled()) {
            logger.trace("Database/Cache Watchdog for `" + tableName + "` table: investigating...");
        }

        String lockName = "rwlock:" + tableName;
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(lockName);
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        try {
            Map<Long, String> checksumFromDatabase = getChecksumFromDatabase();
            List<Long> invalidPrimaryKeys;

            try {
                if (!readWriteLock.readLock().tryLock(5L, 5L, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("`" + lockName + "` read-lock acquisition failure by time-out.");
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("`" + lockName + "` read-lock acquisition failure by interrupt.", e);
            }
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

            try {
                if (!readWriteLock.writeLock().tryLock(5L, 5L, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("`" + lockName + "` write-lock acquisition failure by time-out.");
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("`" + lockName + "` write-lock acquisition failure by interrupt.", e);
            }
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
    }

    private Map<Long, String> getChecksumFromRedis(RedisConnection redisConnection) {
        byte[] checksumKey = serializer.serializeCacheKey(getTableName() + ":checksum");
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
        String underscorePriKeyName = getUnderscorePriKeyName();
        jdbcTemplate.query(checksumQuery, rch -> {
            checksumMap.put(rch.getLong(underscorePriKeyName), rch.getString("checksum"));
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
            logger.trace("Database/Cache Watchdog for `" + getTableName() + "` table: retrieved object data.");
        }

        Map<byte[], byte[]> invalidChecksumMap = new HashMap();
        Map<byte[], byte[]> invalidObjectMap = new HashMap();
        for (Long invalidPrimaryKey : invalidPrimaryKeys) {
            byte[] field = serializer.serializeCacheKey("" + invalidPrimaryKey);

            byte[] checksumValue = serializer.serializeCacheValue(checksumFromDatabase.get(invalidPrimaryKey));
            byte[] objectValue = serializer.serializeCacheValue(objectFromDatabase.get(invalidPrimaryKey));

            invalidChecksumMap.put(field, checksumValue);
            invalidObjectMap.put(field, objectValue);
        }

        byte[] checksumKey = serializer.serializeCacheKey(getTableName() + ":checksum");
        redisConnection.hMSet(checksumKey, invalidChecksumMap);

        byte[] objectKey = serializer.serializeCacheKey(getTableName());
        redisConnection.hMSet(objectKey, invalidObjectMap);
    }

    private Map<Long, T> getObjectFromDatabase() {
        Class<T> mappedClass = getMappedClass();
        String camelCasePriKeyName = getCamelCasePriKeyName();
        return this.delegate.findAll().stream()
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
}
