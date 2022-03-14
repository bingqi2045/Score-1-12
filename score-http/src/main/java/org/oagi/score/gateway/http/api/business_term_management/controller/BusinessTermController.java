package org.oagi.score.gateway.http.api.business_term_management.controller;

import org.oagi.score.repo.api.businessterm.model.*;
import org.oagi.score.service.authentication.AuthenticationService;
import org.oagi.score.service.businessterm.BusinessTermService;
import org.oagi.score.service.common.data.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static org.oagi.score.repo.api.base.SortDirection.ASC;
import static org.oagi.score.repo.api.base.SortDirection.DESC;

@RestController
public class BusinessTermController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private BusinessTermService businessTermService;

    @RequestMapping(value = "/business_terms", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<BusinessTerm> getBusinessTermList(
            @AuthenticationPrincipal AuthenticatedPrincipal requester,
            @RequestParam(name = "term", required = false) String term,
            @RequestParam(name = "updaterUsernameList", required = false) String updaterUsernameList,
            @RequestParam(name = "updateStart", required = false) String updateStart,
            @RequestParam(name = "updateEnd", required = false) String updateEnd,
            @RequestParam(name = "sortActive") String sortActive,
            @RequestParam(name = "sortDirection") String sortDirection,
            @RequestParam(name = "pageIndex") int pageIndex,
            @RequestParam(name = "pageSize") int pageSize) {

        GetBusinessTermListRequest request = new GetBusinessTermListRequest(
                authenticationService.asScoreUser(requester));

        request.setBusinessTerm(term);
        request.setUpdaterUsernameList(!StringUtils.hasLength(updaterUsernameList) ? Collections.emptyList() :
                Arrays.asList(updaterUsernameList.split(",")).stream().map(e -> e.trim())
                        .filter(e -> StringUtils.hasLength(e)).collect(Collectors.toList()));
        if (StringUtils.hasLength(updateStart)) {
            request.setUpdateStartDate(new Timestamp(Long.valueOf(updateStart)).toLocalDateTime());
        }
        if (StringUtils.hasLength(updateEnd)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(updateEnd));
            calendar.add(Calendar.DATE, 1);
            request.setUpdateEndDate(new Timestamp(calendar.getTimeInMillis()).toLocalDateTime());
        }

        request.setPageIndex(pageIndex);
        request.setPageSize(pageSize);
        request.setSortActive(sortActive);
        request.setSortDirection("asc".equalsIgnoreCase(sortDirection) ? ASC : DESC);

        GetBusinessTermListResponse response = businessTermService.getBusinessTermList(request);

        PageResponse<BusinessTerm> pageResponse = new PageResponse<>();
        pageResponse.setList(response.getResults());
        pageResponse.setPage(response.getPage());
        pageResponse.setSize(response.getSize());
        pageResponse.setLength(response.getLength());
        return pageResponse;
    }
    @RequestMapping(value = "/business_terms/check_uniqueness", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean checkUniqueness(
            @AuthenticationPrincipal AuthenticatedPrincipal requester,
            @RequestBody CreateBusinessTermRequest createBusinessTermRequest) {
        return businessTermService.hasSameTermDefinitionAndExternalRefId(
                createBusinessTermRequest.getBusinessTerm(),
                createBusinessTermRequest.getDefinition(),
                createBusinessTermRequest.getExternalReferenceId()
                );
    }

    @RequestMapping(value = "/business_terms/assign", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<AssignedBusinessTerm> getAssignedBusinessTermList(
            @AuthenticationPrincipal AuthenticatedPrincipal requester,
            @RequestParam(name = "bieId", required = false) Optional<BigInteger> bieId,
            @RequestParam(name = "biePropertyTerm", required = false) String biePropertyTerm,
            @RequestParam(name = "term", required = false) String term,
            @RequestParam(name = "updaterUsernameList", required = false) String updaterUsernameList,
            @RequestParam(name = "updateStart", required = false) String updateStart,
            @RequestParam(name = "updateEnd", required = false) String updateEnd,
            @RequestParam(name = "sortActive") String sortActive,
            @RequestParam(name = "sortDirection") String sortDirection,
            @RequestParam(name = "pageIndex") int pageIndex,
            @RequestParam(name = "pageSize") int pageSize) {

        GetAssignedBusinessTermListRequest request = new GetAssignedBusinessTermListRequest(
                authenticationService.asScoreUser(requester));

        if (bieId.isPresent()) {
            request.setBieId(bieId.get());
        }
        request.setBiePropertyTerm(biePropertyTerm);
        request.setBusinessTerm(term);
        request.setUpdaterUsernameList(!StringUtils.hasLength(updaterUsernameList) ? Collections.emptyList() :
                Arrays.asList(updaterUsernameList.split(",")).stream().map(e -> e.trim())
                        .filter(e -> StringUtils.hasLength(e)).collect(Collectors.toList()));
        if (StringUtils.hasLength(updateStart)) {
            request.setUpdateStartDate(new Timestamp(Long.valueOf(updateStart)).toLocalDateTime());
        }
        if (StringUtils.hasLength(updateEnd)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(updateEnd));
            calendar.add(Calendar.DATE, 1);
            request.setUpdateEndDate(new Timestamp(calendar.getTimeInMillis()).toLocalDateTime());
        }

        request.setPageIndex(pageIndex);
        request.setPageSize(pageSize);
        request.setSortActive(sortActive);
        request.setSortDirection("asc".equalsIgnoreCase(sortDirection) ? ASC : DESC);

        GetAssignedBusinessTermListResponse response = businessTermService.getAssignedBusinessTermList(request);

        PageResponse<AssignedBusinessTerm> pageResponse = new PageResponse<>();
        pageResponse.setList(response.getResults());
        pageResponse.setPage(response.getPage());
        pageResponse.setSize(response.getSize());
        pageResponse.setLength(response.getLength());
        return pageResponse;
    }

    @RequestMapping(value = "/business_term", method = RequestMethod.PUT)
    public ResponseEntity create(
            @AuthenticationPrincipal AuthenticatedPrincipal requester,
            @RequestBody BusinessTerm businessTerm) {

        CreateBusinessTermRequest request =
                new CreateBusinessTermRequest(authenticationService.asScoreUser(requester));
        request.setBusinessTerm(businessTerm.getBusinessTerm());
        request.setDefinition(businessTerm.getDefinition());
        request.setExternalReferenceId(businessTerm.getExternalReferenceId());
        request.setExternalReferenceUri(businessTerm.getExternalReferenceUri());

        CreateBusinessTermResponse response =
                businessTermService.createBusinessTerm(request);

        if (response.getBusinessTermId() != null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/business_term/{id}", method = RequestMethod.POST)
    public ResponseEntity update(
            @PathVariable("id") BigInteger businessTermId,
            @AuthenticationPrincipal AuthenticatedPrincipal requester,
            @RequestBody BusinessTerm businessTerm) {

        UpdateBusinessTermRequest request =
                new UpdateBusinessTermRequest(authenticationService.asScoreUser(requester))
                        .withBusinessTermId(businessTermId);
        request.setBusinessTerm(businessTerm.getBusinessTerm());
        request.setDefinition(businessTerm.getDefinition());
        request.setExternalReferenceId(businessTerm.getExternalReferenceId());
        request.setExternalReferenceUri(businessTerm.getExternalReferenceUri());

        UpdateBusinessTermResponse response =
                businessTermService.updateBusinessTerm(request);

        if (response.getBusinessTermId() != null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/business_term/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(
            @AuthenticationPrincipal AuthenticatedPrincipal requester,
            @PathVariable("id") BigInteger businessTermId) {

        DeleteBusinessTermRequest request =
                new DeleteBusinessTermRequest(authenticationService.asScoreUser(requester))
                        .withBusinessTermIdList(Arrays.asList(businessTermId));

        DeleteBusinessTermResponse response =
                businessTermService.deleteBusinessTerm(request);

        if (response.contains(businessTermId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    public static class DeleteBusinessTermRequestData {
        private List<BigInteger> businessTermIdList = Collections.emptyList();

        public List<BigInteger> getBusinessTermIdList() {
            return businessTermIdList;
        }

        public void setBusinessTermIdList(List<BigInteger> businessTermIdList) {
            this.businessTermIdList = businessTermIdList;
        }
    }

    @RequestMapping(value = "/business_term/delete", method = RequestMethod.POST)
    public ResponseEntity deletes(
            @AuthenticationPrincipal AuthenticatedPrincipal requester,
            @RequestBody DeleteBusinessTermRequestData requestData) {
        DeleteBusinessTermRequest request =
                new DeleteBusinessTermRequest(authenticationService.asScoreUser(requester))
                        .withBusinessTermIdList(requestData.getBusinessTermIdList());

        DeleteBusinessTermResponse response =
                businessTermService.deleteBusinessTerm(request);

        if (response.containsAll(requestData.getBusinessTermIdList())) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


//    todo: maybe use for create assignment
//    @RequestMapping(value = "/business_term/{id}", method = RequestMethod.PUT)
//    public ResponseEntity assign(
//            @AuthenticationPrincipal AuthenticatedPrincipal requester,
//            @PathVariable("id") BigInteger businessTermId,
//            @RequestParam(name = "topLevelAsbiepId", required = true) BigInteger topLevelAsbiepId) {
//
//        businessTermService.assign(businessTermId, topLevelAsbiepId);
//        return ResponseEntity.noContent().build();
//    }

//    todo: maybe use for delete assignment / unassign
//    @RequestMapping(value = "/business_term/{id}", method = RequestMethod.DELETE)
//    public ResponseEntity delete(
//            @AuthenticationPrincipal AuthenticatedPrincipal requester,
//            @PathVariable("id") BigInteger businessTermId,
//            @RequestParam(name = "topLevelAsbiepId", required = false) BigInteger topLevelAsbiepId) {
//
//        if (topLevelAsbiepId != null) {
//            businessTermService.dismiss(businessTermId, topLevelAsbiepId);
//            return ResponseEntity.noContent().build();
//        } else {
//            DeleteBusinessTermRequest request =
//                    new DeleteBusinessTermRequest(authenticationService.asScoreUser(requester))
//                            .withBusinessTermIdList(Arrays.asList(businessTermId));
//
//            DeleteBusinessTermResponse response =
//                    businessTermService.deleteBusinessTerm(request);
//
//            if (response.contains(businessTermId)) {
//                return ResponseEntity.noContent().build();
//            } else {
//                return ResponseEntity.badRequest().build();
//            }
//        }
//
//    }

//    @RequestMapping(value = "/context_scheme_values", method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public PageResponse<ContextSchemeValue> getContextSchemeValueList(
//            @AuthenticationPrincipal AuthenticatedPrincipal requester,
//            @RequestParam(name = "value", required = false) String value,
//            @RequestParam(name = "sortActive") String sortActive,
//            @RequestParam(name = "sortDirection") String sortDirection,
//            @RequestParam(name = "pageIndex") int pageIndex,
//            @RequestParam(name = "pageSize") int pageSize) {
//
//        GetContextSchemeValueListRequest request = new GetContextSchemeValueListRequest(
//                authenticationService.asScoreUser(requester));
//
//        request.setValue(value);
//
//        request.setPageIndex(pageIndex);
//        request.setPageSize(pageSize);
//        request.setSortActive(sortActive);
//        request.setSortDirection("asc".equalsIgnoreCase(sortDirection) ? ASC : DESC);
//
//        GetContextSchemeValueListResponse response = contextSchemeService.getContextSchemeValueList(request);
//
//        PageResponse<ContextSchemeValue> pageResponse = new PageResponse<>();
//        pageResponse.setList(response.getResults());
//        pageResponse.setPage(response.getPage());
//        pageResponse.setSize(response.getSize());
//        pageResponse.setLength(response.getLength());
//        return pageResponse;
//    }
//
    @RequestMapping(value = "/business_term/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BusinessTerm getBusinessTerm(
            @AuthenticationPrincipal AuthenticatedPrincipal requester,
            @PathVariable("id") BigInteger businessTermId) {

        GetBusinessTermRequest request = new GetBusinessTermRequest(
                authenticationService.asScoreUser(requester));
        request.setBusinessTermId(businessTermId);

        GetBusinessTermResponse response =
                businessTermService.getBusinessTerm(request);
        return response.getBusinessTerm();
    }

        @RequestMapping(value = "/business_terms/assign", method = RequestMethod.PUT)
    public ResponseEntity create(
            @AuthenticationPrincipal AuthenticatedPrincipal requester,
            @RequestBody AssignBusinessTermRequest request) {

        AssignBusinessTermResponse response =
                businessTermService.assignBusinessTerm(request);

        if (response.getAssignedBusinessTerm() != null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

//    @RequestMapping(value = "/simple_context_schemes", method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public List<ContextScheme> getSimpleContextSchemeList(
//            @AuthenticationPrincipal AuthenticatedPrincipal requester) {
//
//        GetContextSchemeListRequest request = new GetContextSchemeListRequest(
//                authenticationService.asScoreUser(requester));
//        request.setPageIndex(-1);
//        request.setPageSize(-1);
//
//        GetContextSchemeListResponse response = contextSchemeService.getContextSchemeList(request);
//        return response.getResults();
//    }
//
//    @RequestMapping(value = "/context_category/{id}/simple_context_schemes", method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public List<ContextScheme> getSimpleContextSchemeList(
//            @AuthenticationPrincipal AuthenticatedPrincipal requester,
//            @PathVariable("id") BigInteger contextCategoryId) {
//
//        GetContextSchemeListRequest request = new GetContextSchemeListRequest(
//                authenticationService.asScoreUser(requester));
//        request.setContextCategoryIdList(Arrays.asList(contextCategoryId));
//        request.setPageIndex(-1);
//        request.setPageSize(-1);
//
//        GetContextSchemeListResponse response = contextSchemeService.getContextSchemeList(request);
//        return response.getResults();
//    }
//
//    @RequestMapping(value = "/context_scheme", method = RequestMethod.PUT)
//    public ResponseEntity create(
//            @AuthenticationPrincipal AuthenticatedPrincipal requester,
//            @RequestBody ContextScheme contextScheme) {
//
//        CreateContextSchemeRequest request =
//                new CreateContextSchemeRequest(authenticationService.asScoreUser(requester));
//        request.setContextCategoryId(contextScheme.getContextCategoryId());
//        request.setSchemeId(contextScheme.getSchemeId());
//        request.setSchemeName(contextScheme.getSchemeName());
//        request.setSchemeAgencyId(contextScheme.getSchemeAgencyId());
//        request.setSchemeVersionId(contextScheme.getSchemeVersionId());
//        request.setDescription(contextScheme.getDescription());
//        request.setContextSchemeValueList(contextScheme.getContextSchemeValueList());
//
//        CreateContextSchemeResponse response =
//                contextSchemeService.createContextScheme(request);
//
//        if (response.getContextSchemeId() != null) {
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @RequestMapping(value = "/context_scheme/{id}", method = RequestMethod.POST)
//    public ResponseEntity update(
//            @AuthenticationPrincipal AuthenticatedPrincipal requester,
//            @PathVariable("id") BigInteger contextSchemeId,
//            @RequestBody ContextScheme contextScheme) {
//
//        UpdateContextSchemeRequest request =
//                new UpdateContextSchemeRequest(authenticationService.asScoreUser(requester))
//                        .withContextSchemeId(contextSchemeId)
//                        .withContextCategoryId(contextScheme.getContextCategoryId())
//                        .withCodeListId(contextScheme.getCodeListId())
//                        .withSchemeId(contextScheme.getSchemeId())
//                        .withSchemeName(contextScheme.getSchemeName())
//                        .withSchemeAgencyId(contextScheme.getSchemeAgencyId())
//                        .withSchemeVersionId(contextScheme.getSchemeVersionId())
//                        .withDescription(contextScheme.getDescription())
//                        .withContextSchemeValueList(contextScheme.getContextSchemeValueList());
//
//        UpdateContextSchemeResponse response =
//                contextSchemeService.updateContextScheme(request);
//
//        if (response.getContextSchemeId() != null) {
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @RequestMapping(value = "/context_scheme/{id}", method = RequestMethod.DELETE)
//    public ResponseEntity delete(
//            @AuthenticationPrincipal AuthenticatedPrincipal requester,
//            @PathVariable("id") BigInteger contextSchemeId) {
//
//        DeleteContextSchemeRequest request =
//                new DeleteContextSchemeRequest(authenticationService.asScoreUser(requester))
//                        .withContextSchemeId(contextSchemeId);
//
//        DeleteContextSchemeResponse response =
//                contextSchemeService.deleteContextScheme(request);
//
//        if (response.contains(contextSchemeId)) {
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    public static class DeleteContextSchemeRequestData {
//        private List<BigInteger> contextSchemeIdList = Collections.emptyList();
//
//        public List<BigInteger> getContextSchemeIdList() {
//            return contextSchemeIdList;
//        }
//
//        public void setContextSchemeIdList(List<BigInteger> contextSchemeIdList) {
//            this.contextSchemeIdList = contextSchemeIdList;
//        }
//    }
//
//    @RequestMapping(value = "/context_scheme/delete", method = RequestMethod.POST)
//    public ResponseEntity deletes(
//            @AuthenticationPrincipal AuthenticatedPrincipal requester,
//            @RequestBody DeleteContextSchemeRequestData requestData) {
//        DeleteContextSchemeRequest request =
//                new DeleteContextSchemeRequest(authenticationService.asScoreUser(requester))
//                        .withContextSchemeIdList(requestData.getContextSchemeIdList());
//
//        DeleteContextSchemeResponse response =
//                contextSchemeService.deleteContextScheme(request);
//
//        if (response.containsAll(requestData.getContextSchemeIdList())) {
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//

//
//    @RequestMapping(value = "/context_scheme/check_name_uniqueness", method = RequestMethod.POST,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public boolean checkNameUniqueness(
//            @AuthenticationPrincipal AuthenticatedPrincipal requester,
//            @RequestBody ContextScheme contextScheme) {
//        return contextSchemeService.hasSameCtxSchemeName(contextScheme);
//    }
//
//    @RequestMapping(value = "/context_scheme/{id}/simple_context_scheme_values", method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public Collection<ContextSchemeValue> getSimpleContextSchemeValueList(
//            @AuthenticationPrincipal AuthenticatedPrincipal requester,
//            @PathVariable("id") BigInteger contextSchemeId) {
//
//        GetContextSchemeRequest request = new GetContextSchemeRequest(
//                authenticationService.asScoreUser(requester));
//        request.setContextSchemeId(contextSchemeId);
//
//        GetContextSchemeResponse response =
//                contextSchemeService.getContextScheme(request);
//        return response.getContextScheme().getContextSchemeValueList();
//    }
}
