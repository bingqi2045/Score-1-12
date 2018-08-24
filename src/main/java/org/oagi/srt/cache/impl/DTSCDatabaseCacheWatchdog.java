package org.oagi.srt.cache.impl;

import org.oagi.srt.cache.DatabaseCacheWatchdog;
import org.oagi.srt.data.DTSC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DTSCDatabaseCacheWatchdog extends DatabaseCacheWatchdog<DTSC> {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public DTSCDatabaseCacheWatchdog(@Autowired RedisCacheConfiguration redisCacheConfiguration) {
        super("dt_sc", DTSC.class, redisCacheConfiguration);
    }

    private String GET_DT_SC_STATEMENT = "SELECT `dt_sc_id`,`guid`,`property_term`,`representation_term`," +
            "`definition`,`definition_source`,`owner_dt_id`,`cardinality_min`,`cardinality_max`,`based_dt_sc_id` " +
            "FROM `dt_sc`";

    @Override
    protected List<DTSC> findAll() {
        return jdbcTemplate.query(GET_DT_SC_STATEMENT, new BeanPropertyRowMapper<>(DTSC.class));
    }
}
