package org.oagi.srt.cache.impl;

import org.oagi.srt.cache.DatabaseCacheWatchdog;
import org.oagi.srt.data.BCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BCCDatabaseCacheWatchdog extends DatabaseCacheWatchdog<BCC> {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public BCCDatabaseCacheWatchdog(@Autowired RedisCacheConfiguration redisCacheConfiguration) {
        super("bcc", BCC.class, redisCacheConfiguration);
    }

    private String GET_BCC_STATEMENT = "SELECT `bcc_id`,`guid`,`cardinality_min`,`cardinality_max`,`to_bccp_id`,`from_acc_id`," +
            "`seq_key`,`entity_type`,`den`,`definition`,`definition_source`," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_bcc_id`,`is_deprecated` as deprecated,`is_nillable` as nillable,`default_value` FROM bcc";

    @Override
    protected List<BCC> findAll() {
        return jdbcTemplate.query(GET_BCC_STATEMENT, new BeanPropertyRowMapper<>(BCC.class));
    }
}
