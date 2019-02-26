package org.oagi.srt.gateway.http.api.context_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.context_management.data.*;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class ContextSchemeService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DSLContext dslContext;

    public List<ContextScheme> getContextSchemeList() {
        return dslContext.select(
                CTX_SCHEME.CTX_SCHEME_ID,
                CTX_SCHEME.GUID,
                CTX_SCHEME.SCHEME_NAME,
                CTX_SCHEME.CTX_CATEGORY_ID,
                CTX_CATEGORY.NAME.as("ctx_category_name"),
                CTX_SCHEME.SCHEME_ID,
                CTX_SCHEME.SCHEME_AGENCY_ID,
                CTX_SCHEME.SCHEME_VERSION_ID,
                CTX_SCHEME.CODE_LIST_ID,
                CTX_SCHEME.DESCRIPTION,
                CTX_SCHEME.LAST_UPDATE_TIMESTAMP
        ).from(CTX_SCHEME)
                .join(CTX_CATEGORY).on(CTX_SCHEME.CTX_CATEGORY_ID.equal(CTX_CATEGORY.CTX_CATEGORY_ID))
                .fetchInto(ContextScheme.class);
    }

    public ContextScheme getContextScheme(long ctxSchemeId) {
        ContextScheme contextScheme = dslContext.select(
                CTX_SCHEME.CTX_SCHEME_ID,
                CTX_SCHEME.GUID,
                CTX_SCHEME.SCHEME_NAME,
                CTX_SCHEME.CTX_CATEGORY_ID,
                CTX_CATEGORY.NAME.as("ctx_category_name"),
                CTX_SCHEME.SCHEME_ID,
                CTX_SCHEME.SCHEME_AGENCY_ID,
                CTX_SCHEME.SCHEME_VERSION_ID,
                CTX_SCHEME.CODE_LIST_ID,
                CTX_SCHEME.DESCRIPTION,
                CTX_SCHEME.LAST_UPDATE_TIMESTAMP
        ).from(CTX_SCHEME)
                .join(CTX_CATEGORY).on(CTX_SCHEME.CTX_CATEGORY_ID.equal(CTX_CATEGORY.CTX_CATEGORY_ID))
                .where(CTX_SCHEME.CTX_SCHEME_ID.eq(ULong.valueOf(ctxSchemeId)))
                .fetchOneInto(ContextScheme.class);
        contextScheme.setCtxSchemeValues(getContextSchemeValuesByOwnerCtxSchemeId(ctxSchemeId));
        return contextScheme;
    }

    public List<ContextSchemeValue> getContextSchemeValuesByOwnerCtxSchemeId(long ctxSchemeId) {
        return dslContext.select(
                CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID,
                CTX_SCHEME_VALUE.GUID,
                CTX_SCHEME_VALUE.VALUE,
                CTX_SCHEME_VALUE.MEANING
        ).from(CTX_SCHEME_VALUE)
                .where(CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID.eq(ULong.valueOf(ctxSchemeId)))
                .fetchInto(ContextSchemeValue.class);
    }

    public ContextSchemeValue getSimpleContextSchemeValueByCtxSchemeValuesId(long ctxSchemeValuesId) {
        return dslContext.select(
                CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID,
                CTX_SCHEME_VALUE.GUID,
                CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID
                ).from(CTX_SCHEME_VALUE)
                .where(CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID.eq(ULong.valueOf(ctxSchemeValuesId)))
                .fetchOneInto(ContextSchemeValue.class);
    }

    public List<SimpleContextScheme> getSimpleContextSchemeList(long ctxCategoryId) {
        return dslContext.select(
                CTX_SCHEME.CTX_SCHEME_ID,
                CTX_SCHEME.SCHEME_NAME,
                CTX_SCHEME.SCHEME_ID,
                CTX_SCHEME.SCHEME_AGENCY_ID,
                CTX_SCHEME.SCHEME_VERSION_ID
        ).from(CTX_SCHEME)
                .where(CTX_SCHEME.CTX_CATEGORY_ID.eq(ULong.valueOf(ctxCategoryId)))
                .fetchInto(SimpleContextScheme.class);
    }

    public List<BusinessContextValue> getBizCtxValueFromCtxSchemeValueId (long ctxSchemeValueId){
        return dslContext.select(
                BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID,
                BIZ_CTX_VALUE.BIZ_CTX_ID,
                BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID
        ).from(BIZ_CTX_VALUE)
                .where(BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID.eq(ULong.valueOf(ctxSchemeValueId)))
                .fetchInto(BusinessContextValue.class);
    }

    public List<BusinessContextValue> getBizCtxValues () {
        return dslContext.select(
                BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID,
                BIZ_CTX_VALUE.BIZ_CTX_ID,
                BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID
        ).from(BIZ_CTX_VALUE)
                .fetchInto(BusinessContextValue.class);
    }

    public BusinessContext getBusinessContext(long bizCtxId) {
        return dslContext.select(
                BIZ_CTX.BIZ_CTX_ID,
                BIZ_CTX.GUID,
                BIZ_CTX.NAME,
                BIZ_CTX.LAST_UPDATE_TIMESTAMP
        ).from(BIZ_CTX)
                .where(BIZ_CTX.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .fetchOneInto(BusinessContext.class);
    }

    @Transactional
    public void insert(User user, ContextScheme contextScheme) {
        if (StringUtils.isEmpty(contextScheme.getGuid())) {
            contextScheme.setGuid(SrtGuid.randomGuid());
        }

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("ctx_scheme")
                .usingColumns("guid", "scheme_name", "ctx_category_id", "code_list_id",
                        "scheme_id", "scheme_agency_id", "scheme_version_id", "description",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp")
                .usingGeneratedKeyColumns("ctx_scheme_id");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        if (contextScheme.getCodeListId() == 0) {
            MapSqlParameterSource parameterSource = newSqlParameterSource()
                    .addValue("guid", contextScheme.getGuid())
                    .addValue("scheme_name", contextScheme.getSchemeName())
                    .addValue("ctx_category_id", contextScheme.getCtxCategoryId())
                    .addValue("scheme_id", contextScheme.getSchemeId())
                    .addValue("scheme_agency_id", contextScheme.getSchemeAgencyId())
                    .addValue("scheme_version_id", contextScheme.getSchemeVersionId())
                    .addValue("description", contextScheme.getDescription())
                    .addValue("created_by", userId)
                    .addValue("last_updated_by", userId)
                    .addValue("creation_timestamp", timestamp)
                    .addValue("last_update_timestamp", timestamp);
            long ctxSchemeId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
            for (ContextSchemeValue contextSchemeValue : contextScheme.getCtxSchemeValues()) {
                insert(ctxSchemeId, contextSchemeValue);
            }
        } else {
            MapSqlParameterSource parameterSource = newSqlParameterSource()
                    .addValue("guid", contextScheme.getGuid())
                    .addValue("scheme_name", contextScheme.getSchemeName())
                    .addValue("ctx_category_id", contextScheme.getCtxCategoryId())
                    .addValue("scheme_id", contextScheme.getSchemeId())
                    .addValue("scheme_agency_id", contextScheme.getSchemeAgencyId())
                    .addValue("scheme_version_id", contextScheme.getSchemeVersionId())
                    .addValue("code_list_id", contextScheme.getCodeListId())
                    .addValue("description", contextScheme.getDescription())
                    .addValue("created_by", userId)
                    .addValue("last_updated_by", userId)
                    .addValue("creation_timestamp", timestamp)
                    .addValue("last_update_timestamp", timestamp);
            long ctxSchemeId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
            for (ContextSchemeValue contextSchemeValue : contextScheme.getCtxSchemeValues()) {
                insert(ctxSchemeId, contextSchemeValue);
            }
        }

    }

    @Transactional
    public void insert(long ctxSchemeId, ContextSchemeValue contextSchemeValue) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("ctx_scheme_value")
                .usingColumns("guid", "value", "meaning", "owner_ctx_scheme_id");

        if (StringUtils.isEmpty(contextSchemeValue.getGuid())) {
            contextSchemeValue.setGuid(SrtGuid.randomGuid());
        }

        jdbcInsert.execute(newSqlParameterSource()
                .addValue("guid", contextSchemeValue.getGuid())
                .addValue("value", contextSchemeValue.getValue())
                .addValue("meaning", contextSchemeValue.getMeaning())
                .addValue("owner_ctx_scheme_id", ctxSchemeId));
    }

    private String UPDATE_CONTEXT_SCHEME_STATEMENT =
            "UPDATE ctx_scheme SET scheme_name = :scheme_name, ctx_category_id = :ctx_category_id, " +
                    "scheme_id = :scheme_id, scheme_agency_id = :scheme_agency_id, scheme_version_id = :scheme_version_id, " +
                    "description = :description , code_list_id = :code_list_id "+
                    "WHERE ctx_scheme_id = :ctx_scheme_id";

    @Transactional
    public void update(User user, ContextScheme contextScheme) {
        if (contextScheme.getCodeListId() == 0) {
            jdbcTemplate.update(UPDATE_CONTEXT_SCHEME_STATEMENT, newSqlParameterSource()
                    .addValue("ctx_scheme_id", contextScheme.getCtxSchemeId())
                    .addValue("scheme_name", contextScheme.getSchemeName())
                    .addValue("ctx_category_id", contextScheme.getCtxCategoryId())
                    .addValue("scheme_id", contextScheme.getSchemeId())
                    .addValue("scheme_agency_id", contextScheme.getSchemeAgencyId())
                    .addValue("scheme_version_id", contextScheme.getSchemeVersionId())
                    .addValue("code_list_id", null)
                    .addValue("description", contextScheme.getDescription())
                    .addValue("last_updated_by", sessionService.userId(user))
                    .addValue("last_update_timestamp", new Date()));

            update(contextScheme.getCtxSchemeId(), contextScheme.getCtxSchemeValues());
        } else {
            jdbcTemplate.update(UPDATE_CONTEXT_SCHEME_STATEMENT, newSqlParameterSource()
                    .addValue("ctx_scheme_id", contextScheme.getCtxSchemeId())
                    .addValue("scheme_name", contextScheme.getSchemeName())
                    .addValue("ctx_category_id", contextScheme.getCtxCategoryId())
                    .addValue("scheme_id", contextScheme.getSchemeId())
                    .addValue("scheme_agency_id", contextScheme.getSchemeAgencyId())
                    .addValue("scheme_version_id", contextScheme.getSchemeVersionId())
                    .addValue("code_list_id", contextScheme.getCodeListId())
                    .addValue("description", contextScheme.getDescription())
                    .addValue("last_updated_by", sessionService.userId(user))
                    .addValue("last_update_timestamp", new Date()));

            update(contextScheme.getCtxSchemeId(), contextScheme.getCtxSchemeValues());
        }
    }

    private String GET_CONTEXT_SCHEME_VALUE_ID_LIST_STATEMENT =
            "SELECT ctx_scheme_value_id FROM ctx_scheme_value WHERE owner_ctx_scheme_id = :ctx_scheme_id";

    @Transactional
    public void update(final long ctxSchemeId, List<ContextSchemeValue> contextSchemeValues) {
        List<Long> oldCtxSchemeValueIds = jdbcTemplate.queryForList(
                GET_CONTEXT_SCHEME_VALUE_ID_LIST_STATEMENT,
                newSqlParameterSource().addValue("ctx_scheme_id", ctxSchemeId),
                Long.class);

        Map<Long, ContextSchemeValue> newCtxSchemeValues = contextSchemeValues.stream()
                .filter(e -> e.getCtxSchemeValueId() > 0L)
                .collect(Collectors.toMap(ContextSchemeValue::getCtxSchemeValueId, Function.identity()));

        oldCtxSchemeValueIds.removeAll(newCtxSchemeValues.keySet());
        for (long deleteCtxSchemeValueId : oldCtxSchemeValueIds) {
            delete(ctxSchemeId, deleteCtxSchemeValueId);
        }

        for (ContextSchemeValue contextSchemeValue : newCtxSchemeValues.values()) {
            update(ctxSchemeId, contextSchemeValue);
        }

        for (ContextSchemeValue contextSchemeValue : contextSchemeValues.stream()
                .filter(e -> e.getCtxSchemeValueId() == 0L)
                .collect(Collectors.toList())) {
            insert(ctxSchemeId, contextSchemeValue);
        }
    }

    private String UPDATE_CONTEXT_SCHEME_VALUE_STATEMENT =
            "UPDATE ctx_scheme_value SET `value` = :value, meaning = :meaning " +
                    "WHERE ctx_scheme_value_id = :ctx_scheme_value_id AND owner_ctx_scheme_id = :ctx_scheme_id";

    @Transactional
    public void update(long ctxSchemeId, ContextSchemeValue contextSchemeValue) {
        jdbcTemplate.update(UPDATE_CONTEXT_SCHEME_VALUE_STATEMENT, newSqlParameterSource()
                .addValue("ctx_scheme_value_id", contextSchemeValue.getCtxSchemeValueId())
                .addValue("ctx_scheme_id", ctxSchemeId)
                .addValue("value", contextSchemeValue.getValue())
                .addValue("meaning", contextSchemeValue.getMeaning()));
    }

    private String DELETE_CONTEXT_SCHEME_VALUE_STATEMENT =
            "DELETE FROM ctx_scheme_value " +
                    "WHERE ctx_scheme_value_id = :ctx_scheme_value_id AND owner_ctx_scheme_id = :ctx_scheme_id";

    @Transactional
    public void delete(long ctxSchemeId, long ctxSchemeValueId) {
        jdbcTemplate.update(DELETE_CONTEXT_SCHEME_VALUE_STATEMENT, newSqlParameterSource()
                .addValue("ctx_scheme_value_id", ctxSchemeValueId)
                .addValue("ctx_scheme_id", ctxSchemeId));
    }

    private String DELETE_CONTEXT_SCHEME_VALUES_STATEMENT =
            "DELETE FROM ctx_scheme_value WHERE owner_ctx_scheme_id = :ctx_scheme_id";

    private String DELETE_CONTEXT_SCHEME_STATEMENT =
            "DELETE FROM ctx_scheme WHERE ctx_scheme_id = :ctx_scheme_id";

    @Transactional
    public void delete(long ctxSchemeId) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("ctx_scheme_id", ctxSchemeId);

        jdbcTemplate.update(DELETE_CONTEXT_SCHEME_VALUES_STATEMENT, parameterSource);
        jdbcTemplate.update(DELETE_CONTEXT_SCHEME_STATEMENT, parameterSource);
    }
}
