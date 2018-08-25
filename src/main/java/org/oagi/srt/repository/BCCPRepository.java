package org.oagi.srt.repository;

import org.oagi.srt.data.BCCP;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class BCCPRepository implements SrtRepository<BCCP> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_BCCP_STATEMENT = "SELECT `bccp_id`,`guid`,`property_term`,`representation_term`,`bdt_id`,`den`," +
            "`definition`,`definition_source`,`module_id`,`namespace_id`,`is_deprecated` as deprecated," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_bccp_id`,`is_nillable` as nillable,`default_value` FROM `bccp`";

    @Override
    public List<BCCP> findAll() {
        return jdbcTemplate.queryForList(GET_BCCP_STATEMENT, BCCP.class);
    }

    @Override
    public BCCP findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_BCCP_STATEMENT)
                .append(" WHERE `bccp_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), BCCP.class);
    }
}
