package org.oagi.srt.cache.impl;

import org.oagi.srt.cache.DatabaseCacheWatchdog;
import org.oagi.srt.data.BdtPriRestri;
import org.oagi.srt.data.BdtPriRestri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BdtPriRestriDatabaseCacheWatchdog extends DatabaseCacheWatchdog<BdtPriRestri> {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public BdtPriRestriDatabaseCacheWatchdog(@Autowired RedisCacheConfiguration redisCacheConfiguration) {
        super("bdt_pri_restri", BdtPriRestri.class, redisCacheConfiguration);
    }

    private String GET_BDT_PRI_RESTRI_STATEMENT = "SELECT `bdt_pri_restri_id`,`bdt_id`," +
            "`cdt_awd_pri_xps_type_map_id`,`code_list_id`,`agency_id_list_id`,`is_default` as defaulted " +
            "FROM `bdt_pri_restri`";

    @Override
    protected List<BdtPriRestri> findAll() {
        return jdbcTemplate.query(GET_BDT_PRI_RESTRI_STATEMENT, new BeanPropertyRowMapper<>(BdtPriRestri.class));
    }
}
