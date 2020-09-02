package org.oagi.score.gateway.http.api.context_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.gateway.http.api.common.data.PageRequest;
import org.oagi.score.gateway.http.api.common.data.PageResponse;
import org.oagi.score.gateway.http.api.context_management.data.ContextCategory;
import org.oagi.score.gateway.http.api.context_management.data.ContextCategoryListRequest;
import org.oagi.score.gateway.http.api.context_management.data.ContextScheme;
import org.oagi.score.gateway.http.api.context_management.data.SimpleContextCategory;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.helper.SrtGuid;
import org.oagi.score.repo.ContextCategoryRepository;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.businesscontext.model.GetContextCategoryRequest;
import org.oagi.score.repo.api.businesscontext.model.ListContextCategoryRequest;
import org.oagi.score.repo.api.businesscontext.model.ListContextCategoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.oagi.score.repo.api.base.SortDirection.ASC;
import static org.oagi.score.repo.api.base.SortDirection.DESC;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.CTX_CATEGORY;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.CTX_SCHEME;

@Service
@Transactional(readOnly = true)
public class ContextCategoryService {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ContextCategoryRepository repository;

    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    @Autowired
    private DSLContext dslContext;

    public PageResponse<ContextCategory> getContextCategoryList(
            AuthenticatedPrincipal requester, ContextCategoryListRequest request) {

        ListContextCategoryRequest listRequest = new ListContextCategoryRequest(sessionService.asScoreUser(requester));
        listRequest.setContextCategoryIds(request.getContextCategoryIds());
        listRequest.setName(request.getName());
        listRequest.setDescription(request.getDescription());
        listRequest.setUpdaterUsernames(request.getUpdaterLoginIds());
        if (request.getUpdateStartDate() != null) {
            listRequest.setUpdateStartDate(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime());
        }
        if (request.getUpdateEndDate() != null) {
            listRequest.setUpdateEndDate(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime());
        }
        PageRequest pageRequest = request.getPageRequest();
        if (pageRequest != null) {
            listRequest.setPageIndex(pageRequest.getPageIndex());
            listRequest.setPageSize(pageRequest.getPageSize());
            listRequest.setSortActive(pageRequest.getSortActive());
            listRequest.setSortDirection("asc".equalsIgnoreCase(pageRequest.getSortDirection()) ? ASC : DESC);
        }

        ListContextCategoryResponse listResponse =
                scoreRepositoryFactory.createContextCategoryReadRepository()
                .listContextCategories(listRequest);

        List<ContextCategory> contextCategories =
                listResponse.getResults().stream()
                .map(e -> of(e))
                .collect(Collectors.toList());

        PageResponse<ContextCategory> response = new PageResponse();
        response.setList(contextCategories);
        response.setPage(listResponse.getPage());
        response.setSize(listResponse.getSize());
        response.setLength(listResponse.getLength());
        return response;
    }

    private ContextCategory of(org.oagi.score.repo.api.businesscontext.model.ContextCategory e) {
        if (e == null) {
            return null;
        }

        ContextCategory contextCategory = new ContextCategory();
        contextCategory.setCtxCategoryId(e.getContextCategoryId());
        contextCategory.setGuid(e.getGuid());
        contextCategory.setName(e.getName());
        contextCategory.setDescription(e.getDescription());
        contextCategory.setLastUpdateTimestamp(
                Date.from(e.getLastUpdateTimestamp().atZone(ZoneId.systemDefault()).toInstant())
        );
        contextCategory.setLastUpdateUser(e.getLastUpdatedBy().getUsername());
        contextCategory.setUsed(e.isUsed());
        return contextCategory;
    }

    public ContextCategory getContextCategory(AuthenticatedPrincipal requester, BigInteger ctxCategoryId) {
        GetContextCategoryRequest request =
                new GetContextCategoryRequest(sessionService.asScoreUser(requester))
                        .withContextCategoryId(ctxCategoryId);

        return of(scoreRepositoryFactory.createContextCategoryReadRepository()
                .getContextCategory(request)
                .getContextCategory());
    }

    public List<SimpleContextCategory> getSimpleContextCategoryList() {
        return dslContext.select(
                CTX_CATEGORY.CTX_CATEGORY_ID,
                CTX_CATEGORY.NAME
        ).from(CTX_CATEGORY)
                .fetchInto(SimpleContextCategory.class);
    }

    public List<ContextScheme> getContextSchemeByCategoryId(BigInteger ctxCategoryId) {
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

        repository.updateContextCategory(contextCategory.getCtxCategoryId())
                .setName(contextCategory.getName())
                .setDescription(contextCategory.getDescription())
                .setUserId(userId)
                .setTimestamp(timestamp)
                .execute();
    }

    @Transactional
    public void delete(BigInteger ctxCategoryId) {
        dslContext.deleteFrom(CTX_CATEGORY)
                .where(CTX_CATEGORY.CTX_CATEGORY_ID.eq(ULong.valueOf(ctxCategoryId)))
                .execute();
    }

    @Transactional
    public void delete(List<BigInteger> ctxCategoryIds) {
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
