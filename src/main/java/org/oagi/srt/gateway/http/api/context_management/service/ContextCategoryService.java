package org.oagi.srt.gateway.http.api.context_management.service;

import com.google.common.base.Functions;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.context_management.data.ContextCategory;
import org.oagi.srt.gateway.http.api.context_management.data.ContextScheme;
import org.oagi.srt.gateway.http.api.context_management.data.SimpleContextCategory;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.count;
import static org.oagi.srt.entity.jooq.Tables.CTX_CATEGORY;
import static org.oagi.srt.entity.jooq.Tables.CTX_SCHEME;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class ContextCategoryService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private DSLContext dslContext;

    public List<ContextCategory> getContextCategoryList() {
        Map<Long, ContextCategory> ctxCategoryMap = dslContext.select(
                CTX_CATEGORY.CTX_CATEGORY_ID,
                CTX_CATEGORY.GUID,
                CTX_CATEGORY.NAME,
                CTX_CATEGORY.DESCRIPTION
        ).from(CTX_CATEGORY)
                .fetchInto(ContextCategory.class).stream()
                .collect(Collectors.toMap(ContextCategory::getCtxCategoryId, Functions.identity()));

        dslContext.select(CTX_SCHEME.CTX_CATEGORY_ID,
                coalesce(count(CTX_SCHEME.CTX_SCHEME_ID), 0))
                .from(CTX_SCHEME)
                .groupBy(CTX_SCHEME.CTX_CATEGORY_ID)
                .fetch().stream().forEach(record -> {
            long ctxCategoryId = record.value1().longValue();
            int cnt = record.value2();
            ctxCategoryMap.get(ctxCategoryId).setUsed(cnt > 0);
        });

        return new ArrayList(ctxCategoryMap.values());
    }

    public ContextCategory getContextCategory(long ctxCategoryId) {
        ContextCategory ctxCategory = dslContext.select(
                CTX_CATEGORY.CTX_CATEGORY_ID,
                CTX_CATEGORY.GUID,
                CTX_CATEGORY.NAME,
                CTX_CATEGORY.DESCRIPTION
        ).from(CTX_CATEGORY)
                .where(CTX_CATEGORY.CTX_CATEGORY_ID.eq(ULong.valueOf(ctxCategoryId)))
                .fetchOneInto(ContextCategory.class);

        int cnt = dslContext.select(coalesce(count(CTX_SCHEME.CTX_SCHEME_ID), 0))
                .from(CTX_SCHEME)
                .where(CTX_SCHEME.CTX_CATEGORY_ID.eq(ULong.valueOf(ctxCategoryId)))
                .groupBy(CTX_SCHEME.CTX_CATEGORY_ID)
                .fetchOptionalInto(Integer.class).orElse(0);
        ctxCategory.setUsed(cnt > 0);

        return ctxCategory;
    }

    public List<SimpleContextCategory> getSimpleContextCategoryList() {
        return dslContext.select(
                CTX_CATEGORY.CTX_CATEGORY_ID,
                CTX_CATEGORY.NAME
        ).from(CTX_CATEGORY)
                .fetchInto(SimpleContextCategory.class);
    }

    public List<ContextScheme> getContextSchemeByCategoryId(long ctxCategoryId) {
        return dslContext.select(
                CTX_SCHEME.CTX_SCHEME_ID,
                CTX_SCHEME.GUID,
                CTX_SCHEME.SCHEME_NAME,
                CTX_SCHEME.CTX_CATEGORY_ID,
                CTX_CATEGORY.NAME.as("ctx_category_name"),
                CTX_SCHEME.SCHEME_ID,
                CTX_SCHEME.SCHEME_AGENCY_ID,
                CTX_SCHEME.SCHEME_VERSION_ID,
                CTX_SCHEME.DESCRIPTION,
                CTX_SCHEME.LAST_UPDATE_TIMESTAMP
        ).from(CTX_SCHEME)
                .join(CTX_CATEGORY).on(CTX_SCHEME.CTX_CATEGORY_ID.equal(CTX_CATEGORY.CTX_CATEGORY_ID))
                .where(CTX_SCHEME.CTX_CATEGORY_ID.eq(ULong.valueOf(ctxCategoryId)))
                .fetchInto(ContextScheme.class);
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

    @Transactional
    public void delete(long ctxCategoryId) {
        dslContext.deleteFrom(CTX_CATEGORY)
                .where(CTX_CATEGORY.CTX_CATEGORY_ID.eq(ULong.valueOf(ctxCategoryId)))
                .execute();
    }

    @Transactional
    public void delete(List<Long> ctxCategoryIds) {
        if (ctxCategoryIds == null || ctxCategoryIds.isEmpty()) {
            return;
        }

        dslContext.deleteFrom(CTX_CATEGORY)
                .where(CTX_CATEGORY.CTX_CATEGORY_ID.in(
                        ctxCategoryIds.stream().map(
                                e -> ULong.valueOf(e)).collect(Collectors.toList())
                ))
                .execute();
    }
}
