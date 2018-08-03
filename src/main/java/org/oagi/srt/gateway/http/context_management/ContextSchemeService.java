package org.oagi.srt.gateway.http.context_management;

import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.oagi.srt.gateway.http.security.SessionService;
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

    private String GET_CONTEXT_SCHEME_LIST_STATEMENT =
            "SELECT ctx_scheme_id, ctx_scheme.guid, scheme_name, ctx_scheme.ctx_category_id, ctx_category.name as ctx_category_name," +
                    "scheme_id, scheme_agency_id, scheme_version_id, ctx_scheme.description, last_update_timestamp " +
                    "FROM ctx_scheme JOIN ctx_category ON ctx_scheme.ctx_category_id = ctx_category.ctx_category_id";

    public List<ContextScheme> getContextSchemeList() {
        return jdbcTemplate.queryForList(GET_CONTEXT_SCHEME_LIST_STATEMENT, ContextScheme.class);
    }

    private String GET_CONTEXT_SCHEME_STATEMENT =
            "SELECT ctx_scheme_id, ctx_scheme.guid, scheme_name, ctx_scheme.ctx_category_id, ctx_category.name as ctx_category_name," +
                    "scheme_id, scheme_agency_id, scheme_version_id, ctx_scheme.description " +
                    "FROM ctx_scheme JOIN ctx_category ON ctx_scheme.ctx_category_id = ctx_category.ctx_category_id " +
                    "WHERE ctx_scheme_id = :ctx_scheme_id";

    public ContextScheme getContextScheme(long ctxSchemeId) {
        ContextScheme contextScheme = jdbcTemplate.queryForObject(GET_CONTEXT_SCHEME_STATEMENT,
                newSqlParameterSource().addValue("ctx_scheme_id", ctxSchemeId),
                ContextScheme.class);
        contextScheme.setCtxSchemeValues(getContextSchemeValuesByOwnerCtxSchemeId(ctxSchemeId));
        return contextScheme;
    }

    private String GET_CONTEXT_SCHEME_VALUE_LIST_STATEMENT =
            "SELECT ctx_scheme_value_id, guid, `value`, meaning FROM ctx_scheme_value " +
                    "WHERE owner_ctx_scheme_id = :ctx_scheme_id";

    public List<ContextSchemeValue> getContextSchemeValuesByOwnerCtxSchemeId(long ctxSchemeId) {
        return jdbcTemplate.queryForList(GET_CONTEXT_SCHEME_VALUE_LIST_STATEMENT,
                newSqlParameterSource().addValue("ctx_scheme_id", ctxSchemeId),
                ContextSchemeValue.class);
    }

    private String GET_SIMPLE_CONTEXT_SCHEME_LIST_STATEMENT =
            "SELECT ctx_scheme_id, scheme_name, scheme_id, scheme_agency_id, scheme_version_id " +
                    "FROM ctx_scheme WHERE ctx_category_id = :ctx_category_id";

    public List<SimpleContextScheme> getSimpleContextSchemeList(long ctxCategoryId) {
        return jdbcTemplate.queryForList(GET_SIMPLE_CONTEXT_SCHEME_LIST_STATEMENT,
                newSqlParameterSource().addValue("ctx_category_id", ctxCategoryId),
                SimpleContextScheme.class);
    }

    private String GET_SIMPLE_CONTEXT_SCHEME_VALUE_LIST_STATEMENT =
            "SELECT ctx_scheme_value_id, `value`, meaning FROM ctx_scheme_value " +
                    "WHERE owner_ctx_scheme_id = :ctx_scheme_id";

    public List<SimpleContextSchemeValue> getSimpleContextSchemeValueList(long ctxSchemeId) {
        return jdbcTemplate.queryForList(GET_SIMPLE_CONTEXT_SCHEME_VALUE_LIST_STATEMENT,
                newSqlParameterSource().addValue("ctx_scheme_id", ctxSchemeId),
                SimpleContextSchemeValue.class);
    }

    @Transactional
    public void insert(User user, ContextScheme contextScheme) {
        if (StringUtils.isEmpty(contextScheme.getGuid())) {
            contextScheme.setGuid(SrtGuid.randomGuid());
        }


        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("ctx_scheme")
                .usingColumns("guid", "scheme_name", "ctx_category_id",
                        "scheme_id", "scheme_agency_id", "scheme_version_id", "description",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp")
                .usingGeneratedKeyColumns("ctx_scheme_id");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

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
                    "description = :description " +
                    "WHERE ctx_scheme_id = :ctx_scheme_id";

    @Transactional
    public void update(User user, ContextScheme contextScheme) {
        jdbcTemplate.update(UPDATE_CONTEXT_SCHEME_STATEMENT, newSqlParameterSource()
                .addValue("ctx_scheme_id", contextScheme.getCtxSchemeId())
                .addValue("scheme_name", contextScheme.getSchemeName())
                .addValue("ctx_category_id", contextScheme.getCtxCategoryId())
                .addValue("scheme_id", contextScheme.getSchemeId())
                .addValue("scheme_agency_id", contextScheme.getSchemeAgencyId())
                .addValue("scheme_version_id", contextScheme.getSchemeVersionId())
                .addValue("description", contextScheme.getDescription())
                .addValue("last_updated_by", sessionService.userId(user))
                .addValue("last_update_timestamp", new Date()));

        update(contextScheme.getCtxSchemeId(), contextScheme.getCtxSchemeValues());
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
