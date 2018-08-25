package org.oagi.srt.cache;

import org.oagi.srt.repository.SrtRepository;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CachingRepository<T> extends DatabaseCacheHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private CacheSerializer serializer;

    @Autowired
    private RedissonClient redissonClient;

    private SrtRepository<T> delegate;

    public CachingRepository(String tableName, Class<T> mappedClass, SrtRepository<T> delegate) {
        super(tableName, mappedClass);
        this.delegate = delegate;
    }

    public List<T> findAll() {
        return execute(connection -> {
            byte[] key = serializer.serializeCacheKey(getTableName());
            Map<byte[], byte[]> results = connection.hGetAll(key);
            if (results == null) {
                return Collections.emptyList();
            }

            return results.values().stream()
                    .map(e -> (T) serializer.deserializeCacheValue(e))
                    .collect(Collectors.toList());
        });
    }

    public T findById(long id) {
        return execute(connection -> {
            String checksumFromDatabase = getChecksumFromDatabase(id);
            String checksumFromRedis = checksumFromRedis(connection, id);
            if (!checksumFromDatabase.equals(checksumFromRedis)) {
                T result = this.delegate.findById(id);

                setValue(connection, getTableName() + ":checksum", "" + id, checksumFromDatabase);
                setValue(connection, getTableName(), "" + id, result);

                return result;
            }

            return (T) getValue(connection, getTableName(), "" + id);
        });
    }

    private String getChecksumFromDatabase(long id) {
        StringBuilder query = new StringBuilder(getChecksumByIdQuery());
        return jdbcTemplate.queryForObject(query.toString(),
                new MapSqlParameterSource().addValue("id", id), String.class);
    }

    private String checksumFromRedis(RedisConnection connection, long id) {
        return (String) getValue(connection, getTableName() + ":checksum", "" + id);
    }

    private Object getValue(RedisConnection connection, String keyStr, String fieldStr) {
        byte[] key = serializer.serializeCacheKey(keyStr);
        byte[] field = serializer.serializeCacheKey(fieldStr);
        byte[] value = connection.hGet(key, field);

        return serializer.deserializeCacheValue(value);
    }

    private void setValue(RedisConnection connection, String keyStr, String fieldStr, Object value) {
        byte[] key = serializer.serializeCacheKey(keyStr);
        byte[] field = serializer.serializeCacheKey(fieldStr);
        connection.hSet(key, field, serializer.serializeCacheValue(value));
    }

    protected <R> R execute(Function<RedisConnection, R> callback) {
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        try {
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rwlock:" + getTableName());
            readWriteLock.readLock().lock();
            try {
                return callback.apply(redisConnection);
            } finally {
                readWriteLock.readLock().unlock();
            }
        } finally {
            redisConnection.close();
        }
    }
}
