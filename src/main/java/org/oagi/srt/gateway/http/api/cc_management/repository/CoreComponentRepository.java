package org.oagi.srt.gateway.http.api.cc_management.repository;

import org.oagi.srt.gateway.http.api.cc_management.data.*;
import org.oagi.srt.gateway.http.event.EventListenerContainer;
import org.oagi.srt.gateway.http.event.TableChangeEvent;
import org.oagi.srt.gateway.http.event.TableInitEvent;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Repository
@Transactional(readOnly = true)
public class CoreComponentRepository implements InitializingBean, DisposableBean, Runnable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private EventListenerContainer eventListenerContainer;

    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public void afterPropertiesSet() {
        eventListenerContainer.addMessageListener(this,
                "onTableInitEventReceived", new ChannelTopic("tableInitEvent"));
        eventListenerContainer.addMessageListener(this,
                "onTableChangeEventReceived", new ChannelTopic("tableChangeEvent"));

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(this, 0L, 5L, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
    }

    @Override
    public void run() {
        Arrays.asList("acc", "ascc", "asccp", "bcc", "bccp").stream().forEach(tblName -> ensureTableState(tblName));
    }

    @Transactional
    public void onTableInitEventReceived(TableInitEvent tableInitEvent) {
        RLock lock = redissonClient.getLock("TableInitEvent:" + tableInitEvent.hashCode());
        if (!lock.tryLock()) {
            return;
        }
        try {
            logger.debug("Received TableInitEvent: " + tableInitEvent);

            String tblName = tableInitEvent.getTableName();
            long checksum = tableInitEvent.getChecksum();
            updateTableList(tblName, checksum);

        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public void onTableChangeEventReceived(TableChangeEvent tableChangeEvent) {
        RLock lock = redissonClient.getLock("TableChangeEvent:" + tableChangeEvent.hashCode());
        if (!lock.tryLock()) {
            return;
        }
        try {
            logger.debug("Received TableChangeEvent: " + tableChangeEvent);

            String tblName = tableChangeEvent.getTableName();
            long checksum = tableChangeEvent.getValidChecksum();
            updateTableList(tblName, checksum);

        } finally {
            lock.unlock();
        }
    }

    private void updateTableList(String tblName, long checksum) {
        List data = null;
        switch (tblName) {
            case "acc":
                data = doGetAccList();
                break;

            case "ascc":
                data = doGetAsccList();
                break;

            case "bcc":
                data = doGetBccList();
                break;

            case "asccp":
                data = doGetAsccpList();
                break;

            case "bccp":
                data = doGetBccpList();
                break;
        }

        if (data != null) {
            RReadWriteLock rwLock = redissonClient.getReadWriteLock(tblName + ":list");
            RLock writeLock = rwLock.writeLock();
            writeLock.lock();
            try {
                Cache cache = getCache(tblName);
                cache.put("data", data);
                cache.put("timestamp", System.currentTimeMillis());
                cache.put("Checksum", checksum);
            } finally {
                writeLock.unlock();
            }
        }
    }

    public List<ACC> getAccList() {
        return getList("acc", () -> doGetAccList());
    }

    public List<ASCC> getAsccList() {
        return getList("ascc", () -> doGetAsccList());
    }

    public List<BCC> getBccList() {
        return getList("bcc", () -> doGetBccList());
    }

    public List<ASCCP> getAsccpList() {
        return getList("asccp", () -> doGetAsccpList());
    }

    public List<BCCP> getBccpList() {
        return getList("bccp", () -> doGetBccpList());
    }

    private List getList(String tblName, Callable<List> callable) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(tblName + ":list");
        RLock readLock = rwLock.readLock();
        readLock.lock();
        Cache cache = getCache(tblName);
        Cache.ValueWrapper valueWrapper = null;
        try {
            try {
                valueWrapper = cache.get("data");
            } catch (Exception ignore) {
            }

            if (valueWrapper == null) {
                cache.evict("Checksum"); // turn the dirty flag on
                try {
                    return callable.call();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return (List) valueWrapper.get();
        } finally {
            readLock.unlock();
        }
    }

    private List<ACC> doGetAccList() {
        return jdbcTemplate.queryForList("SELECT `acc_id`,`guid`,`object_class_term`,`den`,`definition`,`definition_source`," +
                "`based_acc_id`,`object_class_qualifier`,`oagis_component_type`,`module_id`,`namespace_id`," +
                "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
                "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
                "`current_acc_id`,`is_deprecated` as deprecated,`is_abstract` FROM `acc`", ACC.class);
    }

    private List<ASCC> doGetAsccList() {
        return jdbcTemplate.queryForList("SELECT `ascc_id`,`guid`,`cardinality_min`,`cardinality_max`,`seq_key`," +
                "`from_acc_id`,`to_asccp_id`,`den`,`definition`,`definition_source`,`is_deprecated` as deprecated," +
                "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
                "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
                "`current_ascc_id` FROM `ascc`", ASCC.class);
    }

    private List<BCC> doGetBccList() {
        return jdbcTemplate.queryForList("SELECT `bcc_id`,`guid`,`cardinality_min`,`cardinality_max`,`to_bccp_id`,`from_acc_id`," +
                "`seq_key`,`entity_type`,`den`,`definition`,`definition_source`," +
                "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
                "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
                "`current_bcc_id`,`is_deprecated` as deprecated,`is_nillable` as nillable,`default_value` FROM bcc", BCC.class);
    }

    private List<ASCCP> doGetAsccpList() {
        return jdbcTemplate.queryForList("SELECT `asccp_id`,`guid`,`property_term`,`definition`,`definition_source`,`role_of_acc_id`,`den`," +
                "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
                "`state`,`module_id`,`namespace_id`,`reusable_indicator`,`is_deprecated` as deprecated," +
                "`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
                "`current_asccp_id`,`is_nillable` as nillable FROM `asccp`", ASCCP.class);
    }

    private List<BCCP> doGetBccpList() {
        return jdbcTemplate.queryForList("SELECT `bccp_id`,`guid`,`property_term`,`representation_term`,`bdt_id`,`den`," +
                "`definition`,`definition_source`,`module_id`,`namespace_id`,`is_deprecated` as deprecated," +
                "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
                "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
                "`current_bccp_id`,`is_nillable` as nillable,`default_value` FROM `bccp`", BCCP.class);
    }

    private Cache getCache(String tblName) {
        return redisCacheManager.getCache("core_components:" + tblName);
    }

    private long getChecksum(String tblName) {
        return namedParameterJdbcTemplate.query("CHECKSUM TABLE " + tblName, rs -> {
            if (rs.next()) {
                return rs.getLong("Checksum");
            }
            throw new IllegalStateException();
        });
    }

    private void ensureTableState(String tblName) {
        long checksum = getChecksum(tblName);
        Long cachedChecksum = getCache(tblName).get("Checksum", Long.class);
        if (cachedChecksum == null) {
            TableInitEvent tableInitEvent = new TableInitEvent(tblName, checksum);
            redisTemplate.convertAndSend("tableInitEvent", tableInitEvent);
        } else if (cachedChecksum != checksum) {
            TableChangeEvent tableChangeEvent = new TableChangeEvent(tblName, checksum);
            redisTemplate.convertAndSend("tableChangeEvent", tableChangeEvent);
        }
    }

    private String getRowChecksum(String tblName, long id) {
        List<String> fields = getFields(tblName);
        final String primaryKeyName = tblName + "_id";
        String query = "SELECT sha1(concat_ws(" + String.join(",", fields) + ")) FROM " +
                tblName + " WHERE " + primaryKeyName + " = :row_id";
        return namedParameterJdbcTemplate.queryForObject(query, new MapSqlParameterSource()
                .addValue("row_id", id), String.class);
    }

    private List<String> getFields(String tblName) {
        List<String> fields = namedParameterJdbcTemplate.query("DESCRIBE " + tblName, rs -> {
            List<String> list = new ArrayList();
            while (rs.next()) {
                String field = rs.getString("Field");
                list.add(field);
            }
            return list;
        });
        return fields;
    }
}
