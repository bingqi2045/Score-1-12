package org.oagi.srt.gateway.http.api.context_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.context_management.data.ContextCategory;
import org.oagi.srt.gateway.http.api.context_management.data.ContextCategoryListRequest;
import org.oagi.srt.gateway.http.api.context_management.data.ContextScheme;
import org.oagi.srt.gateway.http.api.context_management.data.SimpleContextCategory;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.ContextCategoryRepository;
import org.oagi.srt.repo.PaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.oagi.srt.entity.jooq.Tables.CTX_CATEGORY;
import static org.oagi.srt.entity.jooq.Tables.CTX_SCHEME;

@Service
@Transactional(readOnly = true)
public class ContextCategoryService {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ContextCategoryRepository repository;

    @Autowired
    private DSLContext dslContext;

    public PageResponse<ContextCategory> getContextCategoryList(ContextCategoryListRequest request) {
        PageRequest pageRequest = request.getPageRequest();

        PaginationResponse<ContextCategory> result = repository.selectContextCategories()
                .setContextCategoryIds(request.getContextCategoryIds().stream().map(e -> ULong.valueOf(e)).collect(Collectors.toList()))
                .setName(request.getName())
                .setDescription(request.getDescription())
                .setUpdaterIds(request.getUpdaterLoginIds().stream().map(e -> ULong.valueOf(e)).collect(Collectors.toList()))
                .setUpdateDate(request.getUpdateStartDate(), request.getUpdateEndDate())
                .setSort(pageRequest.getSortActive(), pageRequest.getSortDirection())
                .setOffset(pageRequest.getOffset(), pageRequest.getPageSize())
                .fetchInto(ContextCategory.class);

        List<ContextCategory> contextCategories = result.getResult();
        Map<Long, Boolean> usedMap = repository.used(
                contextCategories.stream().map(e -> e.getCtxCategoryId()
                ).collect(Collectors.toList()));
        contextCategories.forEach(e -> {
            e.setUsed(usedMap.getOrDefault(e.getCtxCategoryId(), false));
        });

        PageResponse<ContextCategory> response = new PageResponse();
        response.setList(contextCategories);
        response.setPage(pageRequest.getPageIndex());
        response.setSize(pageRequest.getPageSize());
        response.setLength(result.getPageCount());
        return response;
    }

    public ContextCategory getContextCategory(long ctxCategoryId) {
        ContextCategoryListRequest request = new ContextCategoryListRequest();
        request.setContextCategoryIds(Arrays.asList(ctxCategoryId));
        List<ContextCategory> contextCategories = getContextCategoryList(request).getList();
        return (contextCategories.isEmpty()) ? null : contextCategories.get(0);
    }

    public List<SimpleContextCategory> getSimpleContextCategoryList() {
        return dslContext.select(
                CTX_CATEGORY.CTX_CATEGORY_ID,
                CTX_CATEGORY.NAME
        ).from(CTX_CATEGORY)
                .fetchInto(SimpleContextCategory.class);
    }

    public List<ContextScheme> getContextSchemeByCategoryId(long ctxCategoryId) {
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
                .where(CTX_SCHEME.CTX_CATEGORY_ID.eq(ULong.valueOf(ctxCategoryId)))
                .fetchInto(ContextScheme.class);
    }

    @Transactional
    public void insert(User requester, ContextCategory contextCategory) {
        ULong userId = ULong.valueOf(sessionService.userId(requester));
        LocalDateTime timestamp = LocalDateTime.now();

        repository.insertContextCategory()
                .setGuid(StringUtils.isEmpty(contextCategory.getGuid()) ? SrtGuid.randomGuid() : contextCategory.getGuid())
                .setName(contextCategory.getName())
                .setDescription(contextCategory.getDescription())
                .setUserId(userId)
                .setTimestamp(timestamp)
                .execute();
    }

    @Transactional
    public void update(User requester, ContextCategory contextCategory) {
        ULong userId = ULong.valueOf(sessionService.userId(requester));
        LocalDateTime timestamp = LocalDateTime.now();

        repository.updateContextCategory()
                .setContextCategoryId(contextCategory.getCtxCategoryId())
                .setName(contextCategory.getName())
                .setDescription(contextCategory.getDescription())
                .setUserId(userId)
                .setTimestamp(timestamp)
                .execute();
    }

    @Transactional
    public void delete(long ctxCategoryId) {
        dslContext.deleteFrom(CTX_CATEGORY)
                .where(CTX_CATEGORY.CTX_CATEGORY_ID.eq(ULong.valueOf(ctxCategoryId)))
                .execute();
    }

    @Transactional
    public void delete(List<Long> ctxCategoryIds) {
        if (ctxCategoryIds == null || ctxCategoryIds.isEmpty()) {
            return;
        }

        dslContext.deleteFrom(CTX_CATEGORY)
                .where(CTX_CATEGORY.CTX_CATEGORY_ID.in(
                        ctxCategoryIds.stream().map(
                                e -> ULong.valueOf(e)).collect(Collectors.toList())
                ))
                .execute();
    }
}
