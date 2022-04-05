package org.oagi.score.gateway.http.api.business_term_management.service;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Update;
import org.oagi.score.gateway.http.api.business_term_management.data.AssignedBusinessTermListRecord;
import org.oagi.score.gateway.http.api.business_term_management.data.AssignedBusinessTermListRequest;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.repo.BusinessTermRepository;
import org.oagi.score.repo.PaginationResponse;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.businesscontext.model.GetBusinessContextListRequest;
import org.oagi.score.repo.api.businesscontext.model.GetBusinessContextListResponse;
import org.oagi.score.repo.api.businessterm.model.*;
import org.oagi.score.service.authentication.AuthenticationService;
import org.oagi.score.service.businesscontext.BusinessContextService;
import org.oagi.score.service.common.data.AppUser;
import org.oagi.score.service.common.data.PageRequest;
import org.oagi.score.service.common.data.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.jooq.impl.DSL.trueCondition;

@Service
@Transactional(readOnly = true)
public class BusinessTermService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private BusinessContextService businessContextService;

    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private BusinessTermRepository businessTermRepository;

    public GetBusinessTermResponse getBusinessTerm(GetBusinessTermRequest request) {
        GetBusinessTermResponse response =
                scoreRepositoryFactory.createBusinessTermReadRepository()
                        .getBusinessTerm(request);
        return response;
    }

    public GetBusinessTermListResponse getBusinessTermList(GetBusinessTermListRequest request) {
        GetBusinessTermListResponse response;
        if(request.getAssignedBies() != null && !request.getAssignedBies().isEmpty()) {
            response = scoreRepositoryFactory.createBusinessTermReadRepository()
                    .getBusinessTermListByAssignedBie(request);
        }
        else {
            response = scoreRepositoryFactory.createBusinessTermReadRepository()
                    .getBusinessTermList(request);
        }
        return response;
    }

    @Transactional
    public CreateBusinessTermResponse createBusinessTerm(CreateBusinessTermRequest request) {
        CreateBusinessTermResponse response =
                scoreRepositoryFactory.createBusinessTermWriteRepository().createBusinessTerm(request);
        return response;
    }

    @Transactional
    public UpdateBusinessTermResponse updateBusinessTerm(UpdateBusinessTermRequest request) {
        UpdateBusinessTermResponse response =
                scoreRepositoryFactory.createBusinessTermWriteRepository()
                        .updateBusinessTerm(request);

        return response;
    }

    @Transactional
    public DeleteBusinessTermResponse deleteBusinessTerm(DeleteBusinessTermRequest request) {
        DeleteBusinessTermResponse response =
                scoreRepositoryFactory.createBusinessTermWriteRepository()
                        .deleteBusinessTerm(request);
        return response;
    }

    public AssignedBusinessTerm getBusinessTermAssignment(GetAssignedBusinessTermRequest request) {
        AssignedBusinessTerm response = businessTermRepository.getBusinessTermAssignment(request);
        return response;
    }
    public PageResponse<AssignedBusinessTermListRecord> getBusinessTermAssignmentList(AuthenticatedPrincipal user, AssignedBusinessTermListRequest request) {
        PageRequest pageRequest = request.getPageRequest();
        AppUser requester = sessionService.getAppUser(user);

        PaginationResponse<AssignedBusinessTermListRecord> result = businessTermRepository
                .getBieBiztermList(request, AssignedBusinessTermListRecord.class);

        List<AssignedBusinessTermListRecord> assignedBtList = result.getResult();
        assignedBtList.forEach(assignedBt -> {

            GetBusinessContextListRequest getBusinessContextListRequest =
                    new GetBusinessContextListRequest(authenticationService.asScoreUser(user))
                            .withName(request.getBusinessContext());

            getBusinessContextListRequest.setPageIndex(-1);
            getBusinessContextListRequest.setPageSize(-1);

            GetBusinessContextListResponse getBusinessContextListResponse = businessContextService
                    .getBusinessContextList(getBusinessContextListRequest);

            assignedBt.setBusinessContexts(getBusinessContextListResponse.getResults());

            assignedBt.setIsPrimary(assignedBt.getPrimaryIndicator().equals("1"));
        });

        PageResponse<AssignedBusinessTermListRecord> response = new PageResponse();
        response.setList(assignedBtList);
        response.setPage(pageRequest.getPageIndex());
        response.setSize(pageRequest.getPageSize());
        response.setLength(result.getPageCount());
        return response;
    }

    @Transactional
    public AssignBusinessTermResponse assignBusinessTerm(AssignBusinessTermRequest request) {
        AssignBusinessTermResponse response =
                scoreRepositoryFactory.createBusinessTermAssignmentWriteRepository()
                        .assignBusinessTerm(request);
        return response;
    }

    @Transactional
    public UpdateBusinessTermAssignmentResponse updateBusinessTermAssignment(UpdateBusinessTermAssignmentRequest request) {
        UpdateBusinessTermAssignmentResponse response =
                scoreRepositoryFactory.createBusinessTermAssignmentWriteRepository()
                        .updateBusinessTermAssignment(request);
        return response;
    }

    @Transactional
    public DeleteAssignedBusinessTermResponse deleteBusinessTermAssignment(DeleteAssignedBusinessTermRequest request) {
        DeleteAssignedBusinessTermResponse response =
                scoreRepositoryFactory.createBusinessTermAssignmentWriteRepository()
                        .deleteBusinessTermAssignment(request);
        return response;
    }

    @Transactional
    public boolean checkAssignmentUniqueness(AssignBusinessTermRequest assignBusinessTermRequest) {
        return businessTermRepository.checkAssignmentUniqueness(assignBusinessTermRequest);
    }


    public boolean hasSameTermDefinitionAndExternalRefId(String businessTerm, String definition, String externalRefId) {
    Condition idMatch = trueCondition();
    if (businessTerm != null && definition != null && externalRefId != null) {
        return false;
    } else {
//          todo
//            idMatch = CTX_SCHEME.CTX_SCHEME_ID.notEqual(ULong.valueOf(businessTerm.getContextSchemeId()));
        return false;
    }
    }
//
//        return dslContext.selectCount().from(CTX_SCHEME).where(
//                        and(CTX_SCHEME.SCHEME_ID.eq(contextScheme.getSchemeId()),
//                                CTX_SCHEME.SCHEME_AGENCY_ID.eq(contextScheme.getSchemeAgencyId()),
//                                CTX_SCHEME.SCHEME_VERSION_ID.eq(contextScheme.getSchemeVersionId()),
//                                idMatch))
//                .fetchOneInto(Integer.class) > 0;
//    }
//

    //todo
    public boolean hasSameBusinessTermName(BusinessTerm businessTerm) {
        return false;
    }
//        Condition idMatch = trueCondition();
//        if (businessTerm.getBusinessTermId() != null) {
//            idMatch = CTX_SCHEME.CTX_SCHEME_ID.notEqual(ULong.valueOf(contextScheme.getContextSchemeId()));
//        }
//        return dslContext.selectCount().from(CTX_SCHEME).where(
//                        and(CTX_SCHEME.SCHEME_ID.eq(contextScheme.getSchemeId()),
//                                CTX_SCHEME.SCHEME_AGENCY_ID.eq(contextScheme.getSchemeAgencyId()),
//                                CTX_SCHEME.SCHEME_VERSION_ID.notEqual(contextScheme.getSchemeVersionId()),
//                                CTX_SCHEME.SCHEME_NAME.notEqual(contextScheme.getSchemeName()),
//                                idMatch))
//                .fetchOneInto(Integer.class) > 0;
//    }
}
