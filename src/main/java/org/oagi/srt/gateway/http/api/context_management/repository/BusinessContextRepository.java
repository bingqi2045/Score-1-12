package org.oagi.srt.gateway.http.api.context_management.repository;

import com.google.common.base.Functions;
import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContext;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContextListRequest;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContextValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.count;
import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class BusinessContextRepository {

    @Autowired
    private DSLContext dslContext;

    private SelectOnConditionStep
            <Record5<ULong, String, String, Timestamp, String>> getSelectOnConditionStepForBusinessContext() {
        return dslContext.select(
                BIZ_CTX.BIZ_CTX_ID,
                BIZ_CTX.GUID,
                BIZ_CTX.NAME,
                BIZ_CTX.LAST_UPDATE_TIMESTAMP,
                APP_USER.LOGIN_ID.as("last_update_user"))
                .from(BIZ_CTX)
                .join(APP_USER).on(BIZ_CTX.LAST_UPDATED_BY.eq(APP_USER.APP_USER_ID));
    }

    public PageResponse<BusinessContext> findBusinessContexts(BusinessContextListRequest request) {
        SelectOnConditionStep
                <Record5<ULong, String, String, Timestamp, String>> step = getSelectOnConditionStepForBusinessContext();

        List<Condition> conditions = new ArrayList();
        if (!StringUtils.isEmpty(request.getName())) {
            conditions.add(BIZ_CTX.NAME.contains(request.getName().trim()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(APP_USER.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(BIZ_CTX.LAST_UPDATE_TIMESTAMP.greaterOrEqual(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(BIZ_CTX.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        SelectConnectByStep
                <Record5<ULong, String, String, Timestamp, String>> conditionStep = step.where(conditions);

        PageRequest pageRequest = request.getPageRequest();
        String sortDirection = pageRequest.getSortDirection();
        SortField sortField = null;
        switch (pageRequest.getSortActive()) {
            case "name":
                if ("asc".equals(sortDirection)) {
                    sortField = BIZ_CTX.NAME.asc();
                } else if ("desc".equals(sortDirection)) {
                    sortField = BIZ_CTX.NAME.desc();
                }

                break;

            case "lastUpdateTimestamp":
                if ("asc".equals(sortDirection)) {
                    sortField = BIZ_CTX.LAST_UPDATE_TIMESTAMP.asc();
                } else if ("desc".equals(sortDirection)) {
                    sortField = BIZ_CTX.LAST_UPDATE_TIMESTAMP.desc();
                }

                break;
        }

        SelectWithTiesAfterOffsetStep
                <Record5<ULong, String, String, Timestamp, String>> offsetStep = null;
        if (sortField != null) {
            offsetStep = conditionStep.orderBy(sortField)
                    .limit(pageRequest.getOffset(), pageRequest.getPageSize());
        } else {
            if (pageRequest.getPageIndex() >= 0 && pageRequest.getPageSize() > 0) {
                offsetStep = conditionStep
                        .limit(pageRequest.getOffset(), pageRequest.getPageSize());
            }
        }

        PageResponse<BusinessContext> response = new PageResponse();
        List<BusinessContext> result = (offsetStep != null) ?
                offsetStep.fetchInto(BusinessContext.class) : conditionStep.fetchInto(BusinessContext.class);
        if (!result.isEmpty()) {
            Map<Long, BusinessContext> bixCtxMap = result.stream()
                    .collect(Collectors.toMap(BusinessContext::getBizCtxId, Functions.identity()));

            dslContext.select(ABIE.BIZ_CTX_ID,
                    coalesce(count(ABIE.ABIE_ID), 0))
                    .from(ABIE)
                    .where(ABIE.BIZ_CTX_ID.in(
                            bixCtxMap.keySet().stream().map(e -> ULong.valueOf(e)).collect(Collectors.toList())))
                    .groupBy(ABIE.BIZ_CTX_ID)
                    .fetch().stream().forEach(record -> {
                long bizCtxId = record.value1().longValue();
                int cnt = record.value2();
                bixCtxMap.get(bizCtxId).setUsed(cnt > 0);
            });
        }

        response.setList(result);
        response.setPage(pageRequest.getPageIndex());
        response.setSize(pageRequest.getPageSize());
        response.setLength(dslContext.selectCount()
                .from(BIZ_CTX)
                .join(APP_USER).on(BIZ_CTX.LAST_UPDATED_BY.eq(APP_USER.APP_USER_ID))
                .where(conditions)
                .fetchOptionalInto(Integer.class).orElse(0));

        return response;
    }

    public BusinessContext findBusinessContextByBizCtxId(long bizCtxId) {
        if (bizCtxId <= 0L) {
            return null;
        }
        BusinessContext bizCtx = getSelectOnConditionStepForBusinessContext()
                .where(BIZ_CTX.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .fetchOptionalInto(BusinessContext.class).orElse(null);
        if (bizCtx == null) {
            return null;
        }
        bizCtx.setBizCtxValues(findBusinessContextValuesByBizCtxId(bizCtxId));

        int cnt = dslContext.select(coalesce(count(ABIE.ABIE_ID), 0))
                .from(ABIE)
                .where(ABIE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .groupBy(ABIE.BIZ_CTX_ID)
                .fetchOptionalInto(Integer.class).orElse(0);
        bizCtx.setUsed(cnt > 0);

        return bizCtx;
    }

    private SelectOnConditionStep
            <Record8<ULong, ULong, ULong, String, ULong, String, ULong, String>>
    getSelectOnConditionStepForBusinessContextValue() {
        return dslContext.select(
                BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID,
                BIZ_CTX_VALUE.BIZ_CTX_ID,
                CTX_CATEGORY.CTX_CATEGORY_ID,
                CTX_CATEGORY.NAME.as("ctx_category_name"),
                CTX_SCHEME.CTX_SCHEME_ID,
                CTX_SCHEME.SCHEME_NAME.as("ctx_scheme_name"),
                CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID,
                CTX_SCHEME_VALUE.VALUE.as("ctx_scheme_value")
        ).from(BIZ_CTX_VALUE)
                .join(CTX_SCHEME_VALUE).on(BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID.equal(CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID))
                .join(CTX_SCHEME).on(CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID.equal(CTX_SCHEME.CTX_SCHEME_ID))
                .join(CTX_CATEGORY).on(CTX_SCHEME.CTX_CATEGORY_ID.equal(CTX_CATEGORY.CTX_CATEGORY_ID));
    }

    public List<BusinessContextValue> findBusinessContextValues() {
        return getSelectOnConditionStepForBusinessContextValue()
                .fetchInto(BusinessContextValue.class);
    }

    public List<BusinessContextValue> findBusinessContextValuesByBizCtxId(long bizCtxId) {
        if (bizCtxId <= 0L) {
            return Collections.emptyList();
        }
        return getSelectOnConditionStepForBusinessContextValue()
                .where(BIZ_CTX_VALUE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .fetchInto(BusinessContextValue.class);
    }
}
