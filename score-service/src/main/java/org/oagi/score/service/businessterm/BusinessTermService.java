package org.oagi.score.service.businessterm;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.businesscontext.model.*;
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

import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.trueCondition;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.CTX_SCHEME;

@Service
@Transactional(readOnly = true)
public class BusinessTermService {

    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    @Autowired
    private DSLContext dslContext;

    public GetBusinessTermListResponse getBusinessTermList(GetBusinessTermListRequest request) {
//        GetBusinessTermListResponse response =
//                scoreRepositoryFactory.createBusinessTermReadRepository()
//                        .getBusinessTermList(request);
        List<BusinessTerm> termList = new ArrayList<>();
        termList.add(new BusinessTerm(BigInteger.valueOf(1),
                "term1",
                "definition",
                "http://test.com/123",
                "123",
                "123",
                new Date(Long.valueOf("1639759181187")),
                new ScoreUser(BigInteger.ONE, "oagis", ScoreRole.DEVELOPER ),
                new ScoreUser(BigInteger.ONE, "oagis", ScoreRole.DEVELOPER )));

        GetBusinessTermListResponse response = new GetBusinessTermListResponse(termList, 0, 1, 1);
        return response;
    }

//    public GetContextSchemeValueListResponse getContextSchemeValueList(GetContextSchemeValueListRequest request) {
//        GetContextSchemeValueListResponse response =
//                scoreRepositoryFactory.createContextSchemeReadRepository()
//                        .getContextSchemeValueList(request);
//
//        return response;
//    }
//
    public GetBusinessTermResponse getBusinessTerm(GetBusinessTermRequest request) {
//        GetBusinessTermResponse response =
//                scoreRepositoryFactory.createContextSchemeReadRepository()
//                        .getContextScheme(request);

        GetBusinessTermResponse response = new GetBusinessTermResponse(
                new BusinessTerm(BigInteger.valueOf(1),
                "term1",
                "definition",
                "http://test.com/123",
                "123",
                "123",
                new Date(Long.valueOf("1639759181187")),
                new ScoreUser(BigInteger.ONE, "oagis", ScoreRole.DEVELOPER ),
                new ScoreUser(BigInteger.ONE, "oagis", ScoreRole.DEVELOPER )));

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
//    public boolean hasSameCtxScheme(ContextScheme contextScheme) {
//        Condition idMatch = trueCondition();
//        if (contextScheme.getContextSchemeId() != null) {
//            idMatch = CTX_SCHEME.CTX_SCHEME_ID.notEqual(ULong.valueOf(contextScheme.getContextSchemeId()));
//        }
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
