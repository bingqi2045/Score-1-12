package org.oagi.srt.gateway.http.api.context_management.repository;

import com.google.common.base.Functions;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContext;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContextValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    public List<BusinessContext> findBusinessContexts() {
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

    public BusinessContext findBusinessContextByBizCtxId(long bizCtxId) {
        if (bizCtxId <= 0L) {
            return null;
        }
        BusinessContext bizCtx = dslContext.select(
                BIZ_CTX.BIZ_CTX_ID,
                BIZ_CTX.GUID,
                BIZ_CTX.NAME,
                BIZ_CTX.LAST_UPDATE_TIMESTAMP)
                .from(BIZ_CTX)
                .where(BIZ_CTX.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .fetchOneInto(BusinessContext.class);
        bizCtx.setBizCtxValues(findBusinessContextValuesByBizCtxId(bizCtxId));

        int cnt = dslContext.select(coalesce(count(ABIE.ABIE_ID), 0))
                .from(ABIE)
                .where(ABIE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .groupBy(ABIE.BIZ_CTX_ID)
                .fetchOptionalInto(Integer.class).orElse(0);
        bizCtx.setUsed(cnt > 0);

        return bizCtx;
    }

    public List<BusinessContextValue> findBusinessContextValues() {
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
                .fetchInto(BusinessContextValue.class);
    }

    public List<BusinessContextValue> findBusinessContextValuesByBizCtxId(long bizCtxId) {
        if (bizCtxId <= 0L) {
            return Collections.emptyList();
        }
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
}
