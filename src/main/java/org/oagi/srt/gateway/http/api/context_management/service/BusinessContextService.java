package org.oagi.srt.gateway.http.api.context_management.service;

import org.oagi.srt.gateway.http.api.context_management.data.BusinessContext;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContextValue;
import org.oagi.srt.gateway.http.api.context_management.data.SimpleBusinessContext;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class BusinessContextService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    private String GET_BUSINESS_CONTEXT_LIST_STATEMENT =
            "SELECT biz_ctx_id, guid, `name`, last_update_timestamp FROM biz_ctx";

    public List<BusinessContext> getBusinessContextList() {
        return jdbcTemplate.queryForList(GET_BUSINESS_CONTEXT_LIST_STATEMENT, BusinessContext.class);
    }

    private String GET_BUSINESS_CONTEXT_STATEMENT =
            "SELECT biz_ctx_id, guid, `name`, last_update_timestamp FROM biz_ctx " +
                    "WHERE biz_ctx_id = :biz_ctx_id";

    public BusinessContext getBusinessContext(long bizCtxId) {
        BusinessContext bizCtx =
                jdbcTemplate.queryForObject(GET_BUSINESS_CONTEXT_STATEMENT,
                        newSqlParameterSource().addValue("biz_ctx_id", bizCtxId),
                        BusinessContext.class);

        bizCtx.setBizCtxValues(getBusinessContextValuesByBizCtxId(bizCtxId));
        return bizCtx;
    }

    private String GET_SIMPLE_BUSINESS_CONTEXT_LIST_STATEMENT =
            "SELECT biz_ctx_id, `name`, last_update_timestamp FROM biz_ctx";

    public List<SimpleBusinessContext> getSimpleBusinessContextList() {
        return jdbcTemplate.queryForList(GET_SIMPLE_BUSINESS_CONTEXT_LIST_STATEMENT,
                SimpleBusinessContext.class);
    }

    private String GET_SIMPLE_BUSINESS_CONTEXT_STATEMENT =
            "SELECT biz_ctx_id, `name`, last_update_timestamp FROM biz_ctx WHERE biz_ctx_id = :biz_ctx_id";

    public SimpleBusinessContext getSimpleBusinessContext(long bizCtxId) {
        return jdbcTemplate.queryForObject(GET_SIMPLE_BUSINESS_CONTEXT_STATEMENT,
                newSqlParameterSource().addValue("biz_ctx_id", bizCtxId),
                SimpleBusinessContext.class);
    }

    private String GET_BUSINESS_CONTEXT_VALUE_LIST_STATEMENT =
            "SELECT biz_ctx_value_id, " +
                    "ctx_category.ctx_category_id, ctx_category.name as ctx_category_name, " +
                    "ctx_scheme.ctx_scheme_id, ctx_scheme.scheme_name as ctx_scheme_name, " +
                    "ctx_scheme_value.ctx_scheme_value_id, ctx_scheme_value.value as ctx_scheme_value " +
                    "FROM biz_ctx_value JOIN ctx_scheme_value ON biz_ctx_value.ctx_scheme_value_id = ctx_scheme_value.ctx_scheme_value_id " +
                    "JOIN ctx_scheme ON ctx_scheme_value.owner_ctx_scheme_id = ctx_scheme.ctx_scheme_id " +
                    "JOIN ctx_category ON ctx_scheme.ctx_category_id = ctx_category.ctx_category_id " +
                    "WHERE biz_ctx_id = :biz_ctx_id";

    public List<BusinessContextValue> getBusinessContextValuesByBizCtxId(long bizCtxId) {
        return jdbcTemplate.queryForList(GET_BUSINESS_CONTEXT_VALUE_LIST_STATEMENT,
                newSqlParameterSource().addValue("biz_ctx_id", bizCtxId),
                BusinessContextValue.class);
    }

    @Transactional
    public void insert(User user, BusinessContext bizCtx) {
        if (StringUtils.isEmpty(bizCtx.getGuid())) {
            bizCtx.setGuid(SrtGuid.randomGuid());
        }

        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("biz_ctx")
                .usingColumns("guid", "name",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp")
                .usingGeneratedKeyColumns("biz_ctx_id");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", bizCtx.getGuid())
                .addValue("name", bizCtx.getName())
                .addValue("created_by", userId)
                .addValue("last_updated_by", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp);

        long bizCtxId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        for (BusinessContextValue bizCtxValue : bizCtx.getBizCtxValues()) {
            insert(bizCtxId, bizCtxValue);
        }
    }

    @Transactional
    public void insert(long bizCtxId, BusinessContextValue bizCtxValue) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("biz_ctx_value")
                .usingColumns("biz_ctx_id", "ctx_scheme_value_id");

        jdbcInsert.execute(newSqlParameterSource()
                .addValue("biz_ctx_id", bizCtxId)
                .addValue("ctx_scheme_value_id", bizCtxValue.getCtxSchemeValueId()));
    }

    private String UPDATE_BUSINESS_CONTEXT_STATEMENT =
            "UPDATE biz_ctx SET `name` = :name WHERE biz_ctx_id = :biz_ctx_id";

    @Transactional
    public void update(User user, BusinessContext bizCtx) {
        jdbcTemplate.update(UPDATE_BUSINESS_CONTEXT_STATEMENT, newSqlParameterSource()
                .addValue("biz_ctx_id", bizCtx.getBizCtxId())
                .addValue("name", bizCtx.getName())
                .addValue("last_updated_by", sessionService.userId(user))
                .addValue("last_update_timestamp", new Date()));

        update(bizCtx.getBizCtxId(), bizCtx.getBizCtxValues());
    }

    private String GET_BUSINESS_CONTEXT_VALUE_ID_LIST_STATEMENT =
            "SELECT biz_ctx_value_id FROM biz_ctx_value WHERE biz_ctx_id = :biz_ctx_id";

    @Transactional
    public void update(final long bizCtxId, List<BusinessContextValue> bizCtxValues) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("biz_ctx_id", bizCtxId);

        List<Long> oldBizCtxValueIds =
                jdbcTemplate.queryForList(GET_BUSINESS_CONTEXT_VALUE_ID_LIST_STATEMENT,
                        parameterSource, Long.class);

        Map<Long, BusinessContextValue> newBizCtxValues = bizCtxValues.stream()
                .filter(e -> e.getBizCtxValueId() > 0L)
                .collect(Collectors.toMap(BusinessContextValue::getBizCtxValueId, Function.identity()));

        oldBizCtxValueIds.removeAll(newBizCtxValues.keySet());
        for (long deleteBizCtxValueId : oldBizCtxValueIds) {
            delete(bizCtxId, deleteBizCtxValueId);
        }

        for (BusinessContextValue bizCtxValue : newBizCtxValues.values()) {
            update(bizCtxId, bizCtxValue);
        }

        for (BusinessContextValue bizCtxValue : bizCtxValues.stream()
                .filter(e -> e.getBizCtxValueId() == 0L)
                .collect(Collectors.toList())) {
            insert(bizCtxId, bizCtxValue);
        }
    }

    private String UPDATE_BUSINESS_CONTEXT_VALUE_STATEMENT =
            "UPDATE biz_ctx_value SET ctx_scheme_value_id = :ctx_scheme_value_id " +
                    "WHERE biz_ctx_value_id = :biz_ctx_value_id AND biz_ctx_id = :biz_ctx_id";

    @Transactional
    public void update(long bizCtxId, BusinessContextValue bizCtxValue) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("biz_ctx_value_id", bizCtxValue.getBizCtxValueId())
                .addValue("biz_ctx_id", bizCtxId)
                .addValue("ctx_scheme_value_id", bizCtxValue.getCtxSchemeValueId());

        jdbcTemplate.update(UPDATE_BUSINESS_CONTEXT_VALUE_STATEMENT, parameterSource);
    }

    private String DELETE_BUSINESS_CONTEXT_VALUE_STATEMENT =
            "DELETE FROM biz_ctx_value " +
                    "WHERE biz_ctx_value_id = :biz_ctx_value_id AND biz_ctx_id = :biz_ctx_id";

    @Transactional
    public void delete(long bizCtxId, long bizCtxValueId) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("biz_ctx_value_id", bizCtxValueId)
                .addValue("biz_ctx_id", bizCtxId);

        jdbcTemplate.update(DELETE_BUSINESS_CONTEXT_VALUE_STATEMENT, parameterSource);
    }

    private String DELETE_BUSINESS_CONTEXT_VALUES_STATEMENT =
            "DELETE FROM biz_ctx_value WHERE biz_ctx_id = :biz_ctx_id";

    private String DELETE_BUSINESS_CONTEXT_STATEMENT =
            "DELETE FROM biz_ctx WHERE biz_ctx_id = :biz_ctx_id";

    @Transactional
    public void delete(long bizCtxId) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("biz_ctx_id", bizCtxId);

        jdbcTemplate.update(DELETE_BUSINESS_CONTEXT_VALUES_STATEMENT, parameterSource);
        jdbcTemplate.update(DELETE_BUSINESS_CONTEXT_STATEMENT, parameterSource);
    }
}
