package org.oagi.score.service.businesscontext;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.businesscontext.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.UUID;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.BIZ_CTX_ASSIGNMENT;

@Service
@Transactional(readOnly = true)
public class BusinessContextService {

    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    @Autowired
    private DSLContext dslContext;

    public GetBusinessContextListResponse getBusinessContextList(GetBusinessContextListRequest request) {
        GetBusinessContextListResponse response =
                scoreRepositoryFactory.createBusinessContextReadRepository()
                        .getBusinessContextList(request);

        return response;
    }

    public GetBusinessContextResponse getBusinessContext(GetBusinessContextRequest request) {
        GetBusinessContextResponse response =
                scoreRepositoryFactory.createBusinessContextReadRepository()
                        .getBusinessContext(request);

        return response;
    }

    @Transactional
    public CreateBusinessContextResponse createBusinessContext(CreateBusinessContextRequest request) {
        CreateBusinessContextResponse response =
                scoreRepositoryFactory.createBusinessContextWriteRepository()
                        .createBusinessContext(request);

        return response;
    }

    @Transactional
    public UpdateBusinessContextResponse updateBusinessContext(UpdateBusinessContextRequest request) {
        UpdateBusinessContextResponse response =
                scoreRepositoryFactory.createBusinessContextWriteRepository()
                        .updateBusinessContext(request);

        return response;
    }

    @Transactional
    public DeleteBusinessContextResponse deleteBusinessContext(DeleteBusinessContextRequest request) {
        DeleteBusinessContextResponse response =
                scoreRepositoryFactory.createBusinessContextWriteRepository()
                        .deleteBusinessContext(request);

        return response;
    }

    @Transactional
    public void assign(String bizCtxId, String topLevelAsbiepId) {
        dslContext.insertInto(BIZ_CTX_ASSIGNMENT)
                .set(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ASSIGNMENT_ID, UUID.randomUUID().toString())
                .set(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ASBIEP_ID, topLevelAsbiepId)
                .set(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID, bizCtxId)
                .execute();
    }

    @Transactional
    public void dismiss(String bizCtxId, String topLevelAsbiepId) {
        dslContext.deleteFrom(BIZ_CTX_ASSIGNMENT)
                .where(and(
                        BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId),
                        BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID.eq(bizCtxId)
                ))
                .execute();
    }

}
