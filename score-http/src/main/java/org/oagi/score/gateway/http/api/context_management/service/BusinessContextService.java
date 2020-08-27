package org.oagi.score.gateway.http.api.context_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.gateway.http.api.common.data.PageRequest;
import org.oagi.score.gateway.http.api.common.data.PageResponse;
import org.oagi.score.gateway.http.api.context_management.data.BusinessContext;
import org.oagi.score.gateway.http.api.context_management.data.BusinessContextListRequest;
import org.oagi.score.gateway.http.api.context_management.data.BusinessContextValue;
import org.oagi.score.gateway.http.api.context_management.data.SimpleContextSchemeValue;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.helper.SrtGuid;
import org.oagi.score.repo.BusinessContextRepository;
import org.oagi.score.repo.PaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.entity.jooq.Tables.*;

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
                .setTopLevelAsbiepId(request.getTopLevelAsbiepId())
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

    public BusinessContext getBusinessContext(BigInteger bizCtxId) {
        return repository.findBusinessContextByBizCtxId(bizCtxId);
    }

    public List<BusinessContextValue> getBusinessContextValues() {
        return repository.findBusinessContextValues();
    }

    public List<SimpleContextSchemeValue> getSimpleContextSchemeValueList(BigInteger ctxSchemeId) {
        return repository.selectContextSchemeValues()
                .setContextSchemeId(ctxSchemeId)
                .fetchInto(SimpleContextSchemeValue.class);
    }

    public List<BusinessContextValue> getBusinessContextValuesByBusinessCtxId(BigInteger businessContextId) {
        return repository.selectBusinessContextValues()
                .setBusinessContextId(businessContextId)
                .fetchInto(BusinessContextValue.class);
    }

    @Transactional
    public void insert(AuthenticatedPrincipal user, BusinessContext bizCtx) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        LocalDateTime timestamp = LocalDateTime.now();

        ULong businessContextId = repository.insertBusinessContext()
                .setGuid(StringUtils.isEmpty(bizCtx.getGuid()) ? SrtGuid.randomGuid() : bizCtx.getGuid())
                .setName(bizCtx.getName())
                .setUserId(userId)
                .setTimestamp(timestamp)
                .execute();
        bizCtx.setBizCtxId(businessContextId.toBigInteger());

        for (BusinessContextValue bizCtxValue : bizCtx.getBizCtxValues()) {
            repository.insertBusinessContextValue()
                    .setBusinessContextId(businessContextId)
                    .setContextSchemeValueId(bizCtxValue.getCtxSchemeValueId())
                    .execute();
        }
    }

    @Transactional
    public void update(AuthenticatedPrincipal user, BusinessContext bizCtx) {
        BigInteger userId = sessionService.userId(user);
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
    public void update(final BigInteger bizCtxId, List<BusinessContextValue> bizCtxValues) {
        List<BigInteger> oldBizCtxValueIds = dslContext.select(BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID)
                .from(BIZ_CTX_VALUE)
                .where(BIZ_CTX_VALUE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .fetchInto(BigInteger.class);

        Map<BigInteger, BusinessContextValue> newBizCtxValues = bizCtxValues.stream()
                .filter(e -> e.getBizCtxValueId() != null && e.getBizCtxValueId().longValue() > 0L)
                .collect(Collectors.toMap(BusinessContextValue::getBizCtxValueId, Function.identity()));

        oldBizCtxValueIds.removeAll(newBizCtxValues.keySet());
        for (BigInteger deleteBizCtxValueId : oldBizCtxValueIds) {
            delete(bizCtxId, deleteBizCtxValueId);
        }

        for (BusinessContextValue bizCtxValue : newBizCtxValues.values()) {
            update(bizCtxId, bizCtxValue);
        }

        for (BusinessContextValue bizCtxValue : bizCtxValues.stream()
                .filter(e -> e.getBizCtxValueId() == null)
                .collect(Collectors.toList())) {

            repository.insertBusinessContextValue()
                    .setBusinessContextId(bizCtxId)
                    .setContextSchemeValueId(bizCtxValue.getCtxSchemeValueId())
                    .execute();
        }
    }

    @Transactional
    public void update(BigInteger bizCtxId, BusinessContextValue bizCtxValue) {
        dslContext.update(BIZ_CTX_VALUE)
                .set(BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID, ULong.valueOf(bizCtxValue.getCtxSchemeValueId()))
                .where(and(
                        BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID.eq(ULong.valueOf(bizCtxValue.getBizCtxValueId())),
                        BIZ_CTX_VALUE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId))
                ))
                .execute();
    }

    @Transactional
    public void delete(BigInteger bizCtxId, BigInteger bizCtxValueId) {
        dslContext.deleteFrom(BIZ_CTX_VALUE)
                .where(and(
                        BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID.eq(ULong.valueOf(bizCtxValueId)),
                        BIZ_CTX_VALUE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId))))
                .execute();
    }

    @Transactional
    public void assign(BigInteger bizCtxId, BigInteger topLevelAsbiepId) {
        dslContext.insertInto(BIZ_CTX_ASSIGNMENT)
                .set(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ASBIEP_ID, ULong.valueOf(topLevelAsbiepId))
                .set(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID, ULong.valueOf(bizCtxId))
                .execute();
    }

    @Transactional
    public void dismiss(BigInteger bizCtxId, BigInteger topLevelAsbiepId) {
        dslContext.deleteFrom(BIZ_CTX_ASSIGNMENT)
                .where(and(
                        BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(topLevelAsbiepId)),
                        BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId))
                ))
                .execute();
    }

    @Transactional
    public void delete(BigInteger bizCtxId) {
        dslContext.deleteFrom(BIZ_CTX_VALUE)
                .where(BIZ_CTX_VALUE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .execute();
        dslContext.deleteFrom(BIZ_CTX)
                .where(BIZ_CTX.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .execute();
    }

    @Transactional
    public void delete(List<BigInteger> bizCtxIds) {
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
