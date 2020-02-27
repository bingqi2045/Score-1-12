package org.oagi.srt.gateway.http.api.context_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContext;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContextListRequest;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContextValue;
import org.oagi.srt.gateway.http.api.context_management.data.SimpleContextSchemeValue;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.BusinessContextRepository;
import org.oagi.srt.repo.PaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Service
@Transactional(readOnly = true)
public class BusinessContextService {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private BusinessContextRepository repository;

    public PageResponse<BusinessContext> getBusinessContextList(BusinessContextListRequest request) {
        PageRequest pageRequest = request.getPageRequest();

        PaginationResponse<BusinessContext> result = repository.selectBusinessContexts()
                .setTopLevelAbieId(request.getTopLevelAbieId())
                .setName(request.getName())
                .setBizCtxIds(request.getBizCtxIds().stream().map(e -> ULong.valueOf(e)).collect(Collectors.toList()))
                .setUpdaterIds(request.getUpdaterLoginIds().stream().map(e -> ULong.valueOf(e)).collect(Collectors.toList()))
                .setUpdateDate(request.getUpdateStartDate(), request.getUpdateEndDate())
                .setSort(pageRequest.getSortActive(), pageRequest.getSortDirection())
                .setOffset(pageRequest.getOffset(), pageRequest.getPageSize())
                .fetchInto(BusinessContext.class);

        List<BusinessContext> bizCtxs = result.getResult();
        bizCtxs.stream().forEach(bizCtx -> {
            bizCtx.setUsed(repository.used(bizCtx.getBizCtxId()));
        });

        PageResponse<BusinessContext> response = new PageResponse();
        response.setList(bizCtxs);
        response.setPage(pageRequest.getPageIndex());
        response.setSize(pageRequest.getPageSize());
        response.setLength(result.getPageCount());
        return response;
    }

    public BusinessContext getBusinessContext(long bizCtxId) {
        return repository.findBusinessContextByBizCtxId(bizCtxId);
    }

    public List<BusinessContextValue> getBusinessContextValues() {
        return repository.findBusinessContextValues();
    }

    public List<SimpleContextSchemeValue> getSimpleContextSchemeValueList(long ctxSchemeId) {
        return repository.selectContextSchemeValues()
                .setContextSchemeId(ctxSchemeId)
                .fetchInto(SimpleContextSchemeValue.class);
    }

    public List<BusinessContextValue> getBusinessContextValuesByBusinessCtxId(long businessContextId) {
        return repository.selectBusinessContextValues()
                .setBusinessContextId(businessContextId)
                .fetchInto(BusinessContextValue.class);
    }

    @Transactional
    public void insert(User user, BusinessContext bizCtx) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        LocalDateTime timestamp = LocalDateTime.now();

        ULong businessContextId = repository.insertBusinessContext()
                .setGuid(StringUtils.isEmpty(bizCtx.getGuid()) ? SrtGuid.randomGuid() : bizCtx.getGuid())
                .setName(bizCtx.getName())
                .setUserId(userId)
                .setTimestamp(timestamp)
                .execute();

        for (BusinessContextValue bizCtxValue : bizCtx.getBizCtxValues()) {
            repository.insertBusinessContextValue()
                    .setBusinessContextId(businessContextId)
                    .setContextSchemeValueId(bizCtxValue.getCtxSchemeValueId())
                    .execute();
        }
    }

    @Transactional
    public void update(User user, BusinessContext bizCtx) {
        long userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        repository.updateBusinessContext()
                .setUserId(userId)
                .setTimestamp(timestamp)
                .setName(bizCtx.getName())
                .setBusinessContextId(bizCtx.getBizCtxId())
                .execute();

        update(bizCtx.getBizCtxId(), bizCtx.getBizCtxValues());
    }

    @Transactional
    public void update(final long bizCtxId, List<BusinessContextValue> bizCtxValues) {
        List<Long> oldBizCtxValueIds = dslContext.select(BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID)
                .from(BIZ_CTX_VALUE)
                .where(BIZ_CTX_VALUE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .fetchInto(Long.class);

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

            repository.insertBusinessContextValue()
                    .setBusinessContextId(bizCtxId)
                    .setContextSchemeValueId(bizCtxValue.getCtxSchemeValueId())
                    .execute();
        }
    }

    @Transactional
    public void update(long bizCtxId, BusinessContextValue bizCtxValue) {
        dslContext.update(BIZ_CTX_VALUE)
                .set(BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID, ULong.valueOf(bizCtxValue.getCtxSchemeValueId()))
                .where(and(
                        BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID.eq(ULong.valueOf(bizCtxValue.getBizCtxValueId())),
                        BIZ_CTX_VALUE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId))
                ))
                .execute();
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
    public void assign(long bizCtxId, long topLevelAbieId) {
        dslContext.insertInto(BIZ_CTX_ASSIGNMENT)
                .set(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID, ULong.valueOf(topLevelAbieId))
                .set(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID, ULong.valueOf(bizCtxId))
                .execute();
    }

    @Transactional
    public void dismiss(long bizCtxId, long topLevelAbieId) {
        dslContext.deleteFrom(BIZ_CTX_ASSIGNMENT)
                .where(and(
                        BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                        BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId))
                ))
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
