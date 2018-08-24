package org.oagi.srt.cache.impl;

import org.oagi.srt.cache.DatabaseCacheWatchdog;
import org.oagi.srt.data.BCCP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BCCPDatabaseCacheWatchdog extends DatabaseCacheWatchdog<BCCP> {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public BCCPDatabaseCacheWatchdog(@Autowired RedisCacheConfiguration redisCacheConfiguration) {
        super("bccp", BCCP.class, redisCacheConfiguration);
    }

    private String GET_BCCP_STATEMENT = "SELECT `bccp_id`,`guid`,`property_term`,`representation_term`,`bdt_id`,`den`," +
            "`definition`,`definition_source`,`module_id`,`namespace_id`,`is_deprecated` as deprecated," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_bccp_id`,`is_nillable` as nillable,`default_value` FROM `bccp`";

    @Override
    protected List<BCCP> findAll() {
        return jdbcTemplate.query(GET_BCCP_STATEMENT, new BeanPropertyRowMapper<>(BCCP.class));
    }
}
