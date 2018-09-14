package org.oagi.srt.gateway.http.api.context_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.context_management.data.ContextCategory;
import org.oagi.srt.gateway.http.api.context_management.data.SimpleContextCategory;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.oagi.srt.entity.jooq.Tables.CTX_CATEGORY;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class ContextCategoryService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private DSLContext dslContext;

    public List<ContextCategory> getContextCategoryList() {
        return dslContext.select(
                CTX_CATEGORY.CTX_CATEGORY_ID,
                CTX_CATEGORY.GUID,
                CTX_CATEGORY.NAME,
                CTX_CATEGORY.DESCRIPTION
        ).from(CTX_CATEGORY)
                .fetchInto(ContextCategory.class);
    }

    public List<SimpleContextCategory> getSimpleContextCategoryList() {
        return dslContext.select(
                CTX_CATEGORY.CTX_CATEGORY_ID,
                CTX_CATEGORY.NAME
        ).from(CTX_CATEGORY)
                .fetchInto(SimpleContextCategory.class);
    }

    public ContextCategory getContextCategory(long ctxCategoryId) {
        return dslContext.select(
                CTX_CATEGORY.CTX_CATEGORY_ID,
                CTX_CATEGORY.GUID,
                CTX_CATEGORY.NAME,
                CTX_CATEGORY.DESCRIPTION
        ).from(CTX_CATEGORY)
                .where(CTX_CATEGORY.CTX_CATEGORY_ID.eq(ULong.valueOf(ctxCategoryId)))
                .fetchOneInto(ContextCategory.class);
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
