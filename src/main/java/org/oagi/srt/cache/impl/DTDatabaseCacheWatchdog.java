package org.oagi.srt.cache.impl;

import org.oagi.srt.cache.DatabaseCacheWatchdog;
import org.oagi.srt.data.DT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DTDatabaseCacheWatchdog extends DatabaseCacheWatchdog<DT> {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public DTDatabaseCacheWatchdog(@Autowired RedisCacheConfiguration redisCacheConfiguration) {
        super("dt", DT.class, redisCacheConfiguration);
    }

    private String GET_DT_STATEMENT = "SELECT `dt_id`,`guid`,`type`,`version_num`,`previous_version_dt_id`," +
            "`data_type_term`,`qualifier`,`based_dt_id`,`den`,`content_component_den`," +
            "`definition`,`definition_source`,`content_component_definition`,`revision_doc`,`state`,`module_id`," +
            "`created_by`,`last_updated_by`,`owner_user_id`,`creation_timestamp`,`last_update_timestamp`," +
            "`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_bdt_id`,`is_deprecated` as deprecated FROM `dt`";

    @Override
    protected List<DT> findAll() {
        return jdbcTemplate.query(GET_DT_STATEMENT, new BeanPropertyRowMapper<>(DT.class));
    }
}
