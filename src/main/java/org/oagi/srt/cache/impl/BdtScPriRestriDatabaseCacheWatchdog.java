package org.oagi.srt.cache.impl;

import org.oagi.srt.cache.DatabaseCacheWatchdog;
import org.oagi.srt.data.BdtPriRestri;
import org.oagi.srt.data.BdtScPriRestri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BdtScPriRestriDatabaseCacheWatchdog extends DatabaseCacheWatchdog<BdtScPriRestri> {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public BdtScPriRestriDatabaseCacheWatchdog(@Autowired RedisCacheConfiguration redisCacheConfiguration) {
        super("bdt_sc_pri_restri", BdtScPriRestri.class, redisCacheConfiguration);
    }

    private String GET_BDT_SC_PRI_RESTRI_STATEMENT = "SELECT `bdt_sc_pri_restri_id`,`bdt_sc_id`," +
            "`cdt_sc_awd_pri_xps_type_map_id`,`code_list_id`,`agency_id_list_id`,`is_default` as defaulted " +
            "FROM `bdt_sc_pri_restri`";

    @Override
    protected List<BdtScPriRestri> findAll() {
        return jdbcTemplate.query(GET_BDT_SC_PRI_RESTRI_STATEMENT, new BeanPropertyRowMapper<>(BdtScPriRestri.class));
    }
}
