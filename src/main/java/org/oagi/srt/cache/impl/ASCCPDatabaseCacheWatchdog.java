package org.oagi.srt.cache.impl;

import org.oagi.srt.cache.DatabaseCacheWatchdog;
import org.oagi.srt.data.ASCCP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ASCCPDatabaseCacheWatchdog extends DatabaseCacheWatchdog<ASCCP> {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public ASCCPDatabaseCacheWatchdog(@Autowired RedisCacheConfiguration redisCacheConfiguration) {
        super("asccp", ASCCP.class, redisCacheConfiguration);
    }

    private String GET_ASCCP_STATEMENT = "SELECT `asccp_id`,`guid`,`property_term`,`definition`,`definition_source`,`role_of_acc_id`,`den`," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`module_id`,`namespace_id`,`reusable_indicator`,`is_deprecated` as deprecated," +
            "`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_asccp_id`,`is_nillable` as nillable FROM `asccp`";

    @Override
    protected List<ASCCP> findAll() {
        return jdbcTemplate.query(GET_ASCCP_STATEMENT, new BeanPropertyRowMapper<>(ASCCP.class));
    }
}
