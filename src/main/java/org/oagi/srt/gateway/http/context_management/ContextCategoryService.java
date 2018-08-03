package org.oagi.srt.gateway.http.context_management;

import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class ContextCategoryService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_CONTEXT_CATEGORY_LIST_STATEMENT =
            "SELECT ctx_category_id, guid, `name`, description FROM ctx_category";

    public List<ContextCategory> getContextCategoryList() {
        return jdbcTemplate.queryForList(GET_CONTEXT_CATEGORY_LIST_STATEMENT,
                ContextCategory.class);
    }

    private String GET_SIMPLE_CONTEXT_CATEGORY_LIST_STATEMENT =
            "SELECT ctx_category_id, `name` FROM ctx_category";

    public List<SimpleContextCategory> getSimpleContextCategoryList() {
        return jdbcTemplate.queryForList(GET_SIMPLE_CONTEXT_CATEGORY_LIST_STATEMENT,
                SimpleContextCategory.class);
    }

    private String GET_CONTEXT_CATEGORY_STATEMENT =
            "SELECT ctx_category_id, guid, `name`, description FROM ctx_category " +
                    "WHERE ctx_category_id = :ctx_category_id";

    public ContextCategory getContextCategory(long ctxCategoryId) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("ctx_category_id", ctxCategoryId);

        return jdbcTemplate.queryForObject(GET_CONTEXT_CATEGORY_STATEMENT, parameterSource,
                ContextCategory.class);
    }

    @Transactional
    public void insert(ContextCategory contextCategory) {
        if (StringUtils.isEmpty(contextCategory.getGuid())) {
            contextCategory.setGuid(SrtGuid.randomGuid());
        }

        jdbcTemplate.insert()
                .withTableName("ctx_category")
                .usingColumns("guid", "name", "description")
                .execute(newSqlParameterSource()
                        .addValue("guid", contextCategory.getGuid())
                        .addValue("name", contextCategory.getName())
                        .addValue("description", contextCategory.getDescription()));
    }

    private String UPDATE_CONTEXT_CATEGORY_STATEMENT =
            "UPDATE ctx_category SET `name` = :name, description = :description " +
                    "WHERE ctx_category_id = :ctx_category_id";

    @Transactional
    public void update(ContextCategory contextCategory) {
        jdbcTemplate.update(UPDATE_CONTEXT_CATEGORY_STATEMENT, newSqlParameterSource()
                .addValue("ctx_category_id", contextCategory.getCtxCategoryId())
                .addValue("name", contextCategory.getName())
                .addValue("description", contextCategory.getDescription()));
    }

    private String DELETE_CONTEXT_CATEGORY_STATEMENT =
            "DELETE FROM ctx_category WHERE ctx_category_id = :ctx_category_id";

    @Transactional
    public void delete(long ctxCategoryId) {
        jdbcTemplate.update(DELETE_CONTEXT_CATEGORY_STATEMENT, newSqlParameterSource()
                .addValue("ctx_category_id", ctxCategoryId));
    }
}
