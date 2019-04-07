package org.oagi.srt.repository;

import org.oagi.srt.data.ContextScheme;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class CtxSchemeRepository implements SrtRepository<ContextScheme> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_CONTEXT_SCHEME_STATEMENT = "SELECT `ctx_scheme_id`,`guid`,`scheme_id`,`scheme_name`," +
            "`description`,`scheme_agency_id`,`scheme_version_id`,`ctx_category_id`," +
            "`created_by`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp` FROM `ctx_scheme`";

    @Override
    public List<ContextScheme> findAll() {
        return jdbcTemplate.queryForList(GET_CONTEXT_SCHEME_STATEMENT, ContextScheme.class);
    }

    @Override
    public ContextScheme findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_CONTEXT_SCHEME_STATEMENT)
                .append(" WHERE `ctx_scheme_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), ContextScheme.class);
    }

}
