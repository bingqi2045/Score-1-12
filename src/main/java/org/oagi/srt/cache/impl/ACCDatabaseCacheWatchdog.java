package org.oagi.srt.cache.impl;

import org.oagi.srt.cache.DatabaseCacheWatchdog;
import org.oagi.srt.data.ACC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ACCDatabaseCacheWatchdog extends DatabaseCacheWatchdog<ACC> {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public ACCDatabaseCacheWatchdog(@Autowired RedisCacheConfiguration redisCacheConfiguration) {
        super("acc", ACC.class, redisCacheConfiguration);
    }

    private String GET_ACC_STATEMENT = "SELECT `acc_id`,`guid`,`object_class_term`,`den`,`definition`,`definition_source`," +
            "`based_acc_id`,`object_class_qualifier`,`oagis_component_type`,`module_id`,`namespace_id`," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_acc_id`,`is_deprecated` as deprecated,`is_abstract` as abstracted FROM `acc`";

    @Override
    protected List<ACC> findAll() {
        return jdbcTemplate.query(GET_ACC_STATEMENT, new BeanPropertyRowMapper<>(ACC.class));
    }
}
