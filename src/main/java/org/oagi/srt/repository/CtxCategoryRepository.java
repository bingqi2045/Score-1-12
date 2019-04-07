package org.oagi.srt.repository;

import org.oagi.srt.data.ContextCategory;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class CtxCategoryRepository implements SrtRepository<ContextCategory> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_CONTEXT_CATEGORY_STATEMENT = "SELECT `ctx_category_id`,`guid`,`name`,`description` " +
            "FROM `ctx_category`";

    @Override
    public List<ContextCategory> findAll() {
        return jdbcTemplate.queryForList(GET_CONTEXT_CATEGORY_STATEMENT, ContextCategory.class);
    }

    @Override
    public ContextCategory findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_CONTEXT_CATEGORY_STATEMENT)
                .append(" WHERE `ctx_category_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), ContextCategory.class);
    }
    
}
