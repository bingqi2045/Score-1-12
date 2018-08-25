package org.oagi.srt.repository;

import org.oagi.srt.data.DT;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class DTRepository implements SrtRepository<DT> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_DT_STATEMENT = "SELECT `dt_id`,`guid`,`type`,`version_num`,`previous_version_dt_id`," +
            "`data_type_term`,`qualifier`,`based_dt_id`,`den`,`content_component_den`," +
            "`definition`,`definition_source`,`content_component_definition`,`revision_doc`,`state`,`module_id`," +
            "`created_by`,`last_updated_by`,`owner_user_id`,`creation_timestamp`,`last_update_timestamp`," +
            "`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_bdt_id`,`is_deprecated` as deprecated FROM `dt`";

    @Override
    public List<DT> findAll() {
        return jdbcTemplate.queryForList(GET_DT_STATEMENT, DT.class);
    }

    @Override
    public DT findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_DT_STATEMENT)
                .append(" WHERE `dt_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), DT.class);
    }
}
