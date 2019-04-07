package org.oagi.srt.repository;

import org.oagi.srt.data.ContextSchemeValue;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class CtxSchemeValueRepository implements SrtRepository<ContextSchemeValue> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_CONTEXT_SCHEME_VALUE_STATEMENT = "SELECT `ctx_scheme_value_id`,`guid`,`value`,`meaning`," +
            "`owner_ctx_scheme_id` FROM `ctx_scheme_value`";

    @Override
    public List<ContextSchemeValue> findAll() {
        return jdbcTemplate.queryForList(GET_CONTEXT_SCHEME_VALUE_STATEMENT, ContextSchemeValue.class);
    }

    @Override
    public ContextSchemeValue findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_CONTEXT_SCHEME_VALUE_STATEMENT)
                .append(" WHERE `ctx_scheme_value_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), ContextSchemeValue.class);
    }
}
