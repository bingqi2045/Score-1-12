package org.oagi.srt.repository;

import org.oagi.srt.data.ACC;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class ACCRepository implements SrtRepository<ACC> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_ACC_STATEMENT = "SELECT `acc_id`,`guid`,`object_class_term`,`den`,`definition`,`definition_source`," +
            "`based_acc_id`,`object_class_qualifier`,`oagis_component_type`,`module_id`,`namespace_id`," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_acc_id`,`is_deprecated` as deprecated,`is_abstract` as abstracted FROM `acc`";

    @Override
    public List<ACC> findAll() {
        return jdbcTemplate.queryForList(GET_ACC_STATEMENT, ACC.class);
    }

    @Override
    public ACC findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_ACC_STATEMENT)
                .append(" WHERE `acc_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), ACC.class);
    }
}
