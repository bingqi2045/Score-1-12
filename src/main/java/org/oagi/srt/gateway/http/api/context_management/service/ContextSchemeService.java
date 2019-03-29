package org.oagi.srt.gateway.http.api.context_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.context_management.data.*;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Service
@Transactional(readOnly = true)
public class ContextSchemeService {

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

    public List<BusinessContextValue> getBizCtxValueFromCtxSchemeValueId(long ctxSchemeValueId) {
        return dslContext.select(
                BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID,
                BIZ_CTX_VALUE.BIZ_CTX_ID,
                BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID
        ).from(BIZ_CTX_VALUE)
                .where(BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID.eq(ULong.valueOf(ctxSchemeValueId)))
                .fetchInto(BusinessContextValue.class);
    }

    public List<BusinessContextValue> getBizCtxValues() {
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

        ULong userId = ULong.valueOf(sessionService.userId(user));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        ULong ctxSchemeId = dslContext.insertInto(CTX_SCHEME,
                CTX_SCHEME.GUID,
                CTX_SCHEME.SCHEME_NAME,
                CTX_SCHEME.CTX_CATEGORY_ID,
                CTX_SCHEME.SCHEME_ID,
                CTX_SCHEME.SCHEME_AGENCY_ID,
                CTX_SCHEME.SCHEME_VERSION_ID,
                CTX_SCHEME.DESCRIPTION,
                CTX_SCHEME.CREATED_BY,
                CTX_SCHEME.LAST_UPDATED_BY,
                CTX_SCHEME.CREATION_TIMESTAMP,
                CTX_SCHEME.LAST_UPDATE_TIMESTAMP
        ).values(
                contextScheme.getGuid(),
                contextScheme.getSchemeName(),
                ULong.valueOf(contextScheme.getCtxCategoryId()),
                contextScheme.getSchemeId(),
                contextScheme.getSchemeAgencyId(),
                contextScheme.getSchemeVersionId(),
                contextScheme.getDescription(),
                userId, userId, timestamp, timestamp
        ).returning(CTX_SCHEME.CTX_SCHEME_ID)
                .fetchOne().getValue(CTX_SCHEME.CTX_SCHEME_ID);

        for (ContextSchemeValue contextSchemeValue : contextScheme.getCtxSchemeValues()) {
            insert(ctxSchemeId.longValue(), contextSchemeValue);
        }
    }

    @Transactional
    public void insert(long ctxSchemeId, ContextSchemeValue contextSchemeValue) {
        dslContext.insertInto(CTX_SCHEME_VALUE,
                CTX_SCHEME_VALUE.GUID,
                CTX_SCHEME_VALUE.VALUE,
                CTX_SCHEME_VALUE.MEANING,
                CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID
        ).values(
                contextSchemeValue.getGuid(),
                contextSchemeValue.getValue(),
                contextSchemeValue.getMeaning(),
                ULong.valueOf(ctxSchemeId)
        ).execute();
    }

    @Transactional
    public void update(User user, ContextScheme contextScheme) {
        dslContext.update(CTX_SCHEME)
                .set(CTX_SCHEME.SCHEME_NAME, contextScheme.getSchemeName())
                .set(CTX_SCHEME.CTX_CATEGORY_ID, ULong.valueOf(contextScheme.getCtxCategoryId()))
                .set(CTX_SCHEME.SCHEME_ID, contextScheme.getSchemeId())
                .set(CTX_SCHEME.SCHEME_AGENCY_ID, contextScheme.getSchemeAgencyId())
                .set(CTX_SCHEME.SCHEME_VERSION_ID, contextScheme.getSchemeVersionId())
                .set(CTX_SCHEME.DESCRIPTION, contextScheme.getDescription())
                .set(CTX_SCHEME.LAST_UPDATED_BY, ULong.valueOf(sessionService.userId(user)))
                .set(CTX_SCHEME.LAST_UPDATE_TIMESTAMP, new Timestamp(System.currentTimeMillis()))
                .where(CTX_SCHEME.CTX_SCHEME_ID.eq(ULong.valueOf(contextScheme.getCtxSchemeId())))
                .execute();

        update(contextScheme.getCtxSchemeId(), contextScheme.getCtxSchemeValues());
    }

    @Transactional
    public void update(final long ctxSchemeId, List<ContextSchemeValue> contextSchemeValues) {
        List<ULong> oldCtxSchemeValueIds =
                dslContext.select(CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID).from(CTX_SCHEME_VALUE)
                        .where(CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID.eq(ULong.valueOf(ctxSchemeId)))
                        .fetch(CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID);

        Map<Long, ContextSchemeValue> newCtxSchemeValues = contextSchemeValues.stream()
                .filter(e -> e.getCtxSchemeValueId() > 0L)
                .collect(Collectors.toMap(ContextSchemeValue::getCtxSchemeValueId, Function.identity()));

        oldCtxSchemeValueIds.removeAll(newCtxSchemeValues.keySet().stream()
                .map(e -> ULong.valueOf(e)).collect(Collectors.toList()));
        for (ULong deleteCtxSchemeValueId : oldCtxSchemeValueIds) {
            delete(ctxSchemeId, deleteCtxSchemeValueId.longValue());
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

    @Transactional
    public void update(long ctxSchemeId, ContextSchemeValue contextSchemeValue) {
        dslContext.update(CTX_SCHEME_VALUE)
                .set(CTX_SCHEME_VALUE.VALUE, contextSchemeValue.getValue())
                .set(CTX_SCHEME_VALUE.MEANING, contextSchemeValue.getMeaning())
                .where(and(
                        CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID.eq(ULong.valueOf(contextSchemeValue.getCtxSchemeValueId())),
                        CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID.eq(ULong.valueOf(ctxSchemeId))
                )).execute();
    }

    @Transactional
    public void delete(long ctxSchemeId, long ctxSchemeValueId) {
        dslContext.delete(CTX_SCHEME_VALUE)
                .where(and(
                        CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID.eq(ULong.valueOf(ctxSchemeValueId)),
                        CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID.eq(ULong.valueOf(ctxSchemeId))
                )).execute();
    }

    @Transactional
    public void delete(long ctxSchemeId) {
        dslContext.delete(CTX_SCHEME_VALUE)
                .where(CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID.eq(ULong.valueOf(ctxSchemeId)))
                .execute();
        dslContext.delete(CTX_SCHEME)
                .where(CTX_SCHEME.CTX_SCHEME_ID.eq(ULong.valueOf(ctxSchemeId)))
                .execute();
    }
}
