package org.oagi.score.service.businessterm;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.businessterm.model.*;
import org.oagi.score.repo.api.user.model.ScoreRole;
import org.oagi.score.repo.api.user.model.ScoreUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.trueCondition;

@Service
@Transactional(readOnly = true)
public class BusinessTermService {

    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    @Autowired
    private DSLContext dslContext;

    public GetBusinessTermResponse getBusinessTerm(GetBusinessTermRequest request) {
        GetBusinessTermResponse response =
                scoreRepositoryFactory.createBusinessTermReadRepository()
                        .getBusinessTerm(request);
        return response;
    }

    public GetBusinessTermListResponse getBusinessTermList(GetBusinessTermListRequest request) {
        GetBusinessTermListResponse response =
                scoreRepositoryFactory.createBusinessTermReadRepository()
                        .getBusinessTermList(request);
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

    public GetAssignedBusinessTermListResponse getAssignedBusinessTermList(GetAssignedBusinessTermListRequest request) {
//        GetBusinessTermListResponse response =
//                scoreRepositoryFactory.createBusinessTermReadRepository()
//                        .getBusinessTermList(request);
        List<AssignedBusinessTerm> termList = new ArrayList<>();
        termList.add(new AssignedBusinessTerm(
                BigInteger.valueOf(1),
                false,
                BigInteger.valueOf(1),
                "Test BIE",
                "ABIE",
                BigInteger.valueOf(1),
                "term1",
                "definition",
                "http://test.com/123",
                "1",
                "1",
                new Date(Long.valueOf("1639759181187")),
                new ScoreUser(BigInteger.ONE, "oagis", ScoreRole.DEVELOPER ),
                new ScoreUser(BigInteger.ONE, "oagis", ScoreRole.DEVELOPER )));
        termList.add(new AssignedBusinessTerm(
                BigInteger.valueOf(2),
                true,
                BigInteger.valueOf(2),
                "Release Identifier",
                "ABIE",
                BigInteger.valueOf(2),
                "Release ID",
                "Release Identifier definition.",
                "http://amundsen.local/124",
                "124",
                "124",
                new Date(Long.valueOf("1639759181187")),
                new ScoreUser(BigInteger.ONE, "oagis", ScoreRole.DEVELOPER ),
                new ScoreUser(BigInteger.ONE, "oagis", ScoreRole.DEVELOPER )));
        termList.add(new AssignedBusinessTerm(
                BigInteger.valueOf(3),
                true,
                BigInteger.valueOf(3),
                "Sync Invoice",
                "ABIE",
                BigInteger.valueOf(3),
                "Synchronize",
                "The data model for Synchronization message of available quantity.",
                "http://amundsen.local/123",
                "123",
                "123",
                new Date(Long.valueOf("1639759181187")),
                new ScoreUser(BigInteger.ONE, "oagis", ScoreRole.DEVELOPER ),
                new ScoreUser(BigInteger.ONE, "oagis", ScoreRole.DEVELOPER )));

        if(request.getBieId() != null){
            termList = termList.stream().filter(term -> term.getBieId().equals(request.getBieId())).collect(Collectors.toList());
            termList.stream().forEach(row -> System.out.println("bieid: " + row.getBieId()));
        }

        GetAssignedBusinessTermListResponse response = new GetAssignedBusinessTermListResponse(termList, 0, 1, 1);
        return response;
    }

    public AssignBusinessTermResponse assignBusinessTerm(AssignBusinessTermRequest request) {
//        GetBusinessTermListResponse response =
//                scoreRepositoryFactory.createBusinessTermReadRepository()
//                        .getBusinessTermList(request);
        AssignedBusinessTerm assignedBusinessTerm = new AssignedBusinessTerm(
                BigInteger.valueOf(1),
                true,
                BigInteger.valueOf(1),
                "Sync Invoice",
                "ABIE",
                BigInteger.valueOf(1),
                "term1",
                "definition",
                "http://test.com/123",
                "123",
                "123",
                new Date(Long.valueOf("1639759181187")),
                new ScoreUser(BigInteger.ONE, "oagis", ScoreRole.DEVELOPER ),
                new ScoreUser(BigInteger.ONE, "oagis", ScoreRole.DEVELOPER ));

        AssignBusinessTermResponse response = new AssignBusinessTermResponse(assignedBusinessTerm);
        return response;
    }

//
//    @Transactional
//    public CreateContextSchemeResponse createContextScheme(CreateContextSchemeRequest request) {
//        CreateContextSchemeResponse response =
//                scoreRepositoryFactory.createContextSchemeWriteRepository()
//                        .createContextScheme(request);
//
//        return response;
//    }
//
//    @Transactional
//    public UpdateContextSchemeResponse updateContextScheme(UpdateContextSchemeRequest request) {
//        UpdateContextSchemeResponse response =
//                scoreRepositoryFactory.createContextSchemeWriteRepository()
//                        .updateContextScheme(request);
//
//        return response;
//    }
//
//    @Transactional
//    public DeleteContextSchemeResponse deleteContextScheme(DeleteContextSchemeRequest request) {
//        DeleteContextSchemeResponse response =
//                scoreRepositoryFactory.createContextSchemeWriteRepository()
//                        .deleteContextScheme(request);
//
//        return response;
//    }
//
    public boolean hasSameTermDefinitionAndExternalRefId(String businessTerm, String definition, String externalRefId) {
        Condition idMatch = trueCondition();
        if (businessTerm != null && definition != null && externalRefId != null) {
            return false;
        } else {
//          todo
//            idMatch = CTX_SCHEME.CTX_SCHEME_ID.notEqual(ULong.valueOf(businessTerm.getContextSchemeId()));
            return true;
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
//    public boolean hasSameCtxSchemeName(ContextScheme contextScheme) {
//        Condition idMatch = trueCondition();
//        if (contextScheme.getContextSchemeId() != null) {
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
