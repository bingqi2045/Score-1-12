package org.oagi.srt.repository;

import org.oagi.srt.data.ASCCP;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class ASCCPRepository implements SrtRepository<ASCCP> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_ASCCP_STATEMENT = "SELECT `asccp_id`,`guid`,`property_term`,`definition`,`definition_source`,`role_of_acc_id`,`den`," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`module_id`,`namespace_id`,`reusable_indicator`,`is_deprecated` as deprecated," +
            "`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_asccp_id`,`is_nillable` as nillable FROM `asccp`";

    @Override
    public List<ASCCP> findAll() {
        return jdbcTemplate.queryForList(GET_ASCCP_STATEMENT, ASCCP.class);
    }

    @Override
    public ASCCP findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_ASCCP_STATEMENT)
                .append(" WHERE `asccp_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), ASCCP.class);
    }
}
