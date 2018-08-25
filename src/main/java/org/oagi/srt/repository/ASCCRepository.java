package org.oagi.srt.repository;

import org.oagi.srt.data.ASCC;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class ASCCRepository implements SrtRepository<ASCC> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_ASCC_STATEMENT = "SELECT `ascc_id`,`guid`,`cardinality_min`,`cardinality_max`,`seq_key`," +
            "`from_acc_id`,`to_asccp_id`,`den`,`definition`,`definition_source`,`is_deprecated` as deprecated," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_ascc_id` FROM `ascc`";

    @Override
    public List<ASCC> findAll() {
        return jdbcTemplate.queryForList(GET_ASCC_STATEMENT, ASCC.class);
    }

    @Override
    public ASCC findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_ASCC_STATEMENT)
                .append(" WHERE `ascc_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), ASCC.class);
    }
}
