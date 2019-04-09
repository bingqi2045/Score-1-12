package org.oagi.srt.gateway.http.api.context_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.context_management.data.*;
import org.oagi.srt.gateway.http.api.context_management.repository.BusinessContextRepository;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.oagi.srt.repository.UserRepository;
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

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class BusinessContextService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private BusinessContextRepository repository;

    public PageResponse<BusinessContext> getBusinessContextList(BusinessContextListRequest request) {
        return repository.findBusinessContexts(request);
    }

    public BusinessContext getBusinessContext(long bizCtxId) {
        return repository.findBusinessContextByBizCtxId(bizCtxId);
    }

    public List<BusinessContextValue> getBusinessContextValues() {
        return repository.findBusinessContextValues();
    }

    public List<SimpleBusinessContext> getSimpleBusinessContextList() {
        return repository.getSimpleBusinessContextList();
    }

    public SimpleBusinessContext getSimpleBusinessContext(long bizCtxId) {
        return repository.getSimpleBusinessContext(bizCtxId);
    }

    public List<SimpleContextSchemeValue> getSimpleContextSchemeValueList(long ctxSchemeId) {
        return dslContext.select(
                CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID,
                CTX_SCHEME_VALUE.VALUE,
                CTX_SCHEME_VALUE.MEANING
        ).from(CTX_SCHEME_VALUE)
                .where(CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID.eq(ULong.valueOf(ctxSchemeId)))
                .fetchInto(SimpleContextSchemeValue.class);
    }

    public List<BusinessContextValue> getBusinessContextValuesByBusinessCtxId(long businessCtxID) {
        return dslContext.select(
                BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID,
                BIZ_CTX_VALUE.BIZ_CTX_ID,
                BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID
        ).from(BIZ_CTX_VALUE)
                .where(BIZ_CTX_VALUE.BIZ_CTX_ID.eq(ULong.valueOf(businessCtxID)))
                .fetchInto(BusinessContextValue.class);
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

    @Transactional
    public void delete(long bizCtxId, long bizCtxValueId) {
        dslContext.deleteFrom(BIZ_CTX_VALUE)
                .where(and(
                        BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID.eq(ULong.valueOf(bizCtxValueId)),
                        BIZ_CTX_VALUE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId))))
                .execute();
    }

    @Transactional
    public void delete(long bizCtxId) {
        dslContext.deleteFrom(BIZ_CTX_VALUE)
                .where(BIZ_CTX_VALUE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .execute();
        dslContext.deleteFrom(BIZ_CTX)
                .where(BIZ_CTX.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .execute();
    }

    @Transactional
    public void delete(List<Long> bizCtxIds) {
        if (bizCtxIds == null || bizCtxIds.isEmpty()) {
            return;
        }

        dslContext.deleteFrom(BIZ_CTX_VALUE)
                .where(BIZ_CTX_VALUE.BIZ_CTX_ID.in(
                        bizCtxIds.stream().map(
                                e -> ULong.valueOf(e)).collect(Collectors.toList())
                ))
                .execute();
        dslContext.deleteFrom(BIZ_CTX)
                .where(BIZ_CTX.BIZ_CTX_ID.in(
                        bizCtxIds.stream().map(
                                e -> ULong.valueOf(e)).collect(Collectors.toList())
                ))
                .execute();
    }
}
