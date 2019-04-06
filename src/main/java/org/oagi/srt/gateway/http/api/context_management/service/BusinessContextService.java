package org.oagi.srt.gateway.http.api.context_management.service;

import com.google.common.base.Functions;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.ABIE;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContext;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContextValue;
import org.oagi.srt.gateway.http.api.context_management.data.SimpleBusinessContext;
import org.oagi.srt.gateway.http.api.context_management.data.SimpleContextSchemeValue;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;
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

    public List<BusinessContext> getBusinessContextList() {
        Map<Long, BusinessContext> bixCtxMap = dslContext.select(
                BIZ_CTX.BIZ_CTX_ID,
                BIZ_CTX.GUID,
                BIZ_CTX.NAME,
                BIZ_CTX.LAST_UPDATE_TIMESTAMP)
                .from(BIZ_CTX)
                .fetchInto(BusinessContext.class).stream()
                .collect(Collectors.toMap(BusinessContext::getBizCtxId, Functions.identity()));

        dslContext.select(ABIE.BIZ_CTX_ID,
                coalesce(count(ABIE.ABIE_ID), 0))
                .from(ABIE)
                .groupBy(ABIE.BIZ_CTX_ID)
                .fetch().stream().forEach(record -> {
            long bizCtxId = record.value1().longValue();
            int cnt = record.value2();
            bixCtxMap.get(bizCtxId).setUsed(cnt > 0);
        });

        return new ArrayList(bixCtxMap.values());
    }

    public BusinessContext getBusinessContext(long bizCtxId) {
        BusinessContext bizCtx = dslContext.select(
                BIZ_CTX.BIZ_CTX_ID,
                BIZ_CTX.GUID,
                BIZ_CTX.NAME,
                BIZ_CTX.LAST_UPDATE_TIMESTAMP)
                .from(BIZ_CTX)
                .where(BIZ_CTX.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .fetchOneInto(BusinessContext.class);
        bizCtx.setBizCtxValues(getBusinessContextValuesByBizCtxId(bizCtxId));

        int cnt = dslContext.select(coalesce(count(ABIE.ABIE_ID), 0))
                .from(ABIE)
                .where(ABIE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .groupBy(ABIE.BIZ_CTX_ID)
                .fetchOptionalInto(Integer.class).orElse(0);
        bizCtx.setUsed(cnt > 0);

        return bizCtx;
    }

    public List<SimpleBusinessContext> getSimpleBusinessContextList() {
        return dslContext.select(BIZ_CTX.BIZ_CTX_ID,
                BIZ_CTX.NAME,
                BIZ_CTX.GUID,
                BIZ_CTX.LAST_UPDATE_TIMESTAMP
        ).from(BIZ_CTX)
                .fetchInto(SimpleBusinessContext.class);
    }

    public SimpleBusinessContext getSimpleBusinessContext(long bizCtxId) {
        return dslContext.select(BIZ_CTX.BIZ_CTX_ID,
                BIZ_CTX.NAME,
                BIZ_CTX.GUID,
                BIZ_CTX.LAST_UPDATE_TIMESTAMP
        ).from(BIZ_CTX)
                .where(BIZ_CTX.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .fetchOneInto(SimpleBusinessContext.class);
    }

    public List<BusinessContextValue> getBusinessContextValuesByBizCtxId(long bizCtxId) {
        return dslContext.select(
                BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID,
                CTX_CATEGORY.CTX_CATEGORY_ID,
                CTX_CATEGORY.NAME.as("ctx_category_name"),
                CTX_SCHEME.CTX_SCHEME_ID,
                CTX_SCHEME.SCHEME_NAME.as("ctx_scheme_name"),
                CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID,
                CTX_SCHEME_VALUE.VALUE.as("ctx_scheme_value")
        ).from(BIZ_CTX_VALUE)
                .join(CTX_SCHEME_VALUE).on(BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID.equal(CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID))
                .join(CTX_SCHEME).on(CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID.equal(CTX_SCHEME.CTX_SCHEME_ID))
                .join(CTX_CATEGORY).on(CTX_SCHEME.CTX_CATEGORY_ID.equal(CTX_CATEGORY.CTX_CATEGORY_ID))
                .where(BIZ_CTX_VALUE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .fetchInto(BusinessContextValue.class);
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

    public List<ABIE> getBIEListFromBizCtxId (long businessCtxID) {
        return dslContext.select(
               ABIE.ABIE_ID,
               ABIE.BASED_ACC_ID,
               ABIE.BIZ_CTX_ID,
               ABIE.GUID,
               ABIE.OWNER_TOP_LEVEL_ABIE_ID,
               ABIE.LAST_UPDATED_BY
        ).from(ABIE)
            .where(ABIE.BIZ_CTX_ID.eq(ULong.valueOf(businessCtxID)))
            .fetchInto(ABIE.class);
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
