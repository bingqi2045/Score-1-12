package org.oagi.srt.gateway.http.context_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ContextCategoryService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private String GET_CONTEXT_CATEGORY_LIST_STATEMENT =
            "SELECT ctx_category_id, guid, `name`, description FROM ctx_category";

    public List<ContextCategory> getContextCategoryList() {
        return jdbcTemplate.query(GET_CONTEXT_CATEGORY_LIST_STATEMENT,
                new BeanPropertyRowMapper(ContextCategory.class));
    }

    private String GET_CONTEXT_CATEGORY_STATEMENT =
            "SELECT ctx_category_id, guid, `name`, description FROM ctx_category " +
                    "WHERE ctx_category_id = :ctx_category_id";

    public ContextCategory getContextCategory(long ctxCategoryId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("ctx_category_id", ctxCategoryId);

        List<ContextCategory> res =
                jdbcTemplate.query(GET_CONTEXT_CATEGORY_STATEMENT, parameterSource,
                        new BeanPropertyRowMapper(ContextCategory.class));
        if (res.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }
        return res.get(0);
    }

    private String UPDATE_CONTEXT_CATEGORY_STATEMENT =
            "UPDATE ctx_category SET `name` = :name, description = :description " +
                    "WHERE ctx_category_id = :ctx_category_id";

    @Transactional
    public void update(ContextCategory contextCategory) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("ctx_category_id", contextCategory.getCtxCategoryId());
        parameterSource.addValue("name", contextCategory.getName());
        parameterSource.addValue("description", contextCategory.getDescription());

        jdbcTemplate.update(UPDATE_CONTEXT_CATEGORY_STATEMENT, parameterSource);
    }

    private String DELETE_CONTEXT_CATEGORY_STATEMENT =
            "DELETE FROM ctx_category WHERE ctx_category_id = :ctx_category_id";

    @Transactional
    public void delete(long ctxCategoryId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("ctx_category_id", ctxCategoryId);

        jdbcTemplate.update(DELETE_CONTEXT_CATEGORY_STATEMENT, parameterSource);
    }
}
