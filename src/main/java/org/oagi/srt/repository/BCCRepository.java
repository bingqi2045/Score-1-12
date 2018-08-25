package org.oagi.srt.repository;

import org.oagi.srt.data.BCC;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class BCCRepository implements SrtRepository<BCC> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_BCC_STATEMENT = "SELECT `bcc_id`,`guid`,`cardinality_min`,`cardinality_max`,`to_bccp_id`,`from_acc_id`," +
            "`seq_key`,`entity_type`,`den`,`definition`,`definition_source`," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_bcc_id`,`is_deprecated` as deprecated,`is_nillable` as nillable,`default_value` FROM bcc";

    @Override
    public List<BCC> findAll() {
        return jdbcTemplate.queryForList(GET_BCC_STATEMENT, BCC.class);
    }

    @Override
    public BCC findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_BCC_STATEMENT)
                .append(" WHERE `bcc_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), BCC.class);
    }
}
