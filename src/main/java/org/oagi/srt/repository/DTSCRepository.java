package org.oagi.srt.repository;

import org.oagi.srt.data.DTSC;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class DTSCRepository implements SrtRepository<DTSC> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_DT_SC_STATEMENT = "SELECT `dt_sc_id`,`guid`,`property_term`,`representation_term`," +
            "`definition`,`definition_source`,`owner_dt_id`,`cardinality_min`,`cardinality_max`,`based_dt_sc_id` " +
            "FROM `dt_sc`";

    @Override
    public List<DTSC> findAll() {
        return jdbcTemplate.queryForList(GET_DT_SC_STATEMENT, DTSC.class);
    }

    @Override
    public DTSC findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_DT_SC_STATEMENT)
                .append(" WHERE `dt_sc_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), DTSC.class);
    }
}
