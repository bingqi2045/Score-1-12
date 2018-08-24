package org.oagi.srt.cache.impl;

import org.oagi.srt.cache.DatabaseCacheWatchdog;
import org.oagi.srt.data.ASCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ASCCDatabaseCacheWatchdog extends DatabaseCacheWatchdog<ASCC> {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public ASCCDatabaseCacheWatchdog(@Autowired RedisCacheConfiguration redisCacheConfiguration) {
        super("ascc", ASCC.class, redisCacheConfiguration);
    }

    private String GET_ASCC_STATEMENT = "SELECT `ascc_id`,`guid`,`cardinality_min`,`cardinality_max`,`seq_key`," +
            "`from_acc_id`,`to_asccp_id`,`den`,`definition`,`definition_source`,`is_deprecated` as deprecated," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_ascc_id` FROM `ascc`";

    @Override
    protected List<ASCC> findAll() {
        return jdbcTemplate.query(GET_ASCC_STATEMENT, new BeanPropertyRowMapper<>(ASCC.class));
    }
}
