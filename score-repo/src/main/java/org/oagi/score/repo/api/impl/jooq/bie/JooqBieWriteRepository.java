package org.oagi.score.repo.api.impl.jooq.bie;

import org.jooq.DSLContext;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.bie.BieWriteRepository;
import org.oagi.score.repo.api.bie.model.*;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

public class JooqBieWriteRepository
        extends JooqScoreRepository
        implements BieWriteRepository {

    public JooqBieWriteRepository(DSLContext dslContext) {
        super(dslContext);
    }

    @Override
    public CreateBieResponse createBie(CreateBieRequest request) throws ScoreDataAccessException {
        ScoreUser requester = request.getRequester();
        WrappedAsbiep topLevelAsbiep = request.getTopLevelAsbiep();

        TopLevelAsbiepRecord topLevelAsbiepRecord = insertTopLevelAsbiep(request);
        request.getBizCtxIds().forEach(bizCtxId -> {
            insertBizCtxAssignment(topLevelAsbiepRecord, bizCtxId);
        });
        String topLevelAsbiepId = topLevelAsbiepRecord.getTopLevelAsbiepId();

        insertAbie(topLevelAsbiep.getRoleOfAbie(), requester, topLevelAsbiepId);
        topLevelAsbiep.getAsbiep().setRoleOfAbieId(topLevelAsbiep.getRoleOfAbie().getAbieId());
        insertAsbiep(topLevelAsbiep.getAsbiep(), requester, topLevelAsbiepId);
        dslContext().update(TOP_LEVEL_ASBIEP)
                .set(TOP_LEVEL_ASBIEP.ASBIEP_ID, topLevelAsbiep.getAsbiep().getAsbiepId())
                .where(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepRecord.getTopLevelAsbiepId()))
                .execute();

        request.getAsbieList().forEach(asbie -> {
            if (asbie.getToAsbiep() == null && asbie.getRefTopLevelAsbiepId() == null) {
                return;
            }

            insertAbie(asbie.getFromAbie(), requester, topLevelAsbiepId);
            asbie.getAsbie().setFromAbieId(asbie.getFromAbie().getAbieId());
            if (asbie.getToAsbiep() == null) {
                String toAsbiepId = dslContext().select(TOP_LEVEL_ASBIEP.ASBIEP_ID)
                        .from(TOP_LEVEL_ASBIEP)
                        .where(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(asbie.getRefTopLevelAsbiepId()))
                        .fetchOneInto(String.class);
                asbie.getAsbie().setToAsbiepId(toAsbiepId);
            } else {
                insertAbie(asbie.getToAsbiep().getRoleOfAbie(), requester, topLevelAsbiepId);
                asbie.getToAsbiep().getAsbiep().setRoleOfAbieId(asbie.getToAsbiep().getRoleOfAbie().getAbieId());
                insertAsbiep(asbie.getToAsbiep().getAsbiep(), requester, topLevelAsbiepId);
                asbie.getAsbie().setToAsbiepId(asbie.getToAsbiep().getAsbiep().getAsbiepId());
            }
            insertAsbie(asbie.getAsbie(), requester, topLevelAsbiepId);
        });
        request.getBbieList().forEach(bbie -> {
            insertAbie(bbie.getFromAbie(), requester, topLevelAsbiepId);
            bbie.getBbie().setFromAbieId(bbie.getFromAbie().getAbieId());
            insertBbiep(bbie.getToBbiep(), requester, topLevelAsbiepId);
            bbie.getBbie().setToBbiepId(bbie.getToBbiep().getBbiepId());
            insertBbie(bbie.getBbie(), requester, topLevelAsbiepId);
        });
        request.getBbieScList().forEach(bbieSc -> {
            insertBbie(bbieSc.getBbie(), requester, topLevelAsbiepId);
            bbieSc.getBbieSc().setBbieId(bbieSc.getBbie().getBbieId());
            insertBbieSc(bbieSc.getBbieSc(), requester, topLevelAsbiepId);
        });

        return new CreateBieResponse(topLevelAsbiepId);
    }

    private TopLevelAsbiepRecord insertTopLevelAsbiep(CreateBieRequest request) {
        String userId = request.getRequester().getUserId();

        TopLevelAsbiepRecord topLevelAsbiepRecord = new TopLevelAsbiepRecord();
        topLevelAsbiepRecord.setTopLevelAsbiepId(UUID.randomUUID().toString());
        topLevelAsbiepRecord.setReleaseId(dslContext().select(ASCCP_MANIFEST.RELEASE_ID)
                .from(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(
                        request.getTopLevelAsbiep().getAsbiep().getBasedAsccpManifestId()))
                .fetchOneInto(String.class)
        );
        topLevelAsbiepRecord.setState(BieState.WIP.name());
        topLevelAsbiepRecord.setStatus(request.getStatus());
        topLevelAsbiepRecord.setVersion(request.getVersion());
        topLevelAsbiepRecord.setOwnerUserId(userId);
        topLevelAsbiepRecord.setLastUpdatedBy(userId);
        topLevelAsbiepRecord.setLastUpdateTimestamp(LocalDateTime.now());

        dslContext().insertInto(TOP_LEVEL_ASBIEP)
                .set(topLevelAsbiepRecord)
                .execute();

        return topLevelAsbiepRecord;
    }

    private void insertBizCtxAssignment(TopLevelAsbiepRecord topLevelAsbiepRecord,
                                        String bizCtxId) {

        BizCtxAssignmentRecord bizCtxAssignmentRecord = new BizCtxAssignmentRecord();
        bizCtxAssignmentRecord.setBizCtxAssignmentId(UUID.randomUUID().toString());
        bizCtxAssignmentRecord.setTopLevelAsbiepId(topLevelAsbiepRecord.getTopLevelAsbiepId());
        bizCtxAssignmentRecord.setBizCtxId(bizCtxId);

        dslContext().insertInto(BIZ_CTX_ASSIGNMENT)
                .set(bizCtxAssignmentRecord)
                .execute();
    }

    private void insertAbie(Abie abie, ScoreUser user, String topLevelAsbiepId) {
        if (abie == null) {
            throw new IllegalArgumentException();
        }
        if (abie.getAbieId() != null) {
            return;
        }

        String userId = user.getUserId();

        AbieRecord abieRecord = new AbieRecord();
        abieRecord.setAbieId(UUID.randomUUID().toString());
        abieRecord.setGuid(abie.getGuid());
        abieRecord.setBasedAccManifestId(abie.getBasedAccManifestId());
        abieRecord.setPath(abie.getPath());
        abieRecord.setHashPath(abie.getHashPath());
        abieRecord.setDefinition(abie.getDefinition());
        abieRecord.setCreatedBy(userId);
        abieRecord.setLastUpdatedBy(userId);
        abieRecord.setCreationTimestamp(LocalDateTime.now());
        abieRecord.setLastUpdateTimestamp(LocalDateTime.now());
        abieRecord.setRemark(abie.getRemark());
        abieRecord.setBizTerm(abie.getBizTerm());
        abieRecord.setOwnerTopLevelAsbiepId(topLevelAsbiepId);

        dslContext().insertInto(ABIE)
                .set(abieRecord)
                .execute();
        abie.setAbieId(abieRecord.getAbieId());
    }

    private void insertAsbiep(Asbiep asbiep, ScoreUser user, String topLevelAsbiepId) {
        if (asbiep == null) {
            throw new IllegalArgumentException();
        }
        if (asbiep.getAsbiepId() != null) {
            return;
        }

        String userId = user.getUserId();

        AsbiepRecord asbiepRecord = new AsbiepRecord();
        asbiepRecord.setAsbiepId(UUID.randomUUID().toString());
        asbiepRecord.setGuid(asbiep.getGuid());
        asbiepRecord.setBasedAsccpManifestId(asbiep.getBasedAsccpManifestId());
        asbiepRecord.setPath(asbiep.getPath());
        asbiepRecord.setHashPath(asbiep.getHashPath());
        asbiepRecord.setRoleOfAbieId(asbiep.getRoleOfAbieId());
        asbiepRecord.setDefinition(asbiep.getDefinition());
        asbiepRecord.setRemark(asbiep.getRemark());
        asbiepRecord.setBizTerm(asbiep.getBizTerm());
        asbiepRecord.setCreatedBy(userId);
        asbiepRecord.setLastUpdatedBy(userId);
        asbiepRecord.setCreationTimestamp(LocalDateTime.now());
        asbiepRecord.setLastUpdateTimestamp(LocalDateTime.now());
        asbiepRecord.setOwnerTopLevelAsbiepId(topLevelAsbiepId);

        dslContext().insertInto(ASBIEP)
                .set(asbiepRecord)
                .execute();
        asbiep.setAsbiepId(asbiepRecord.getAsbiepId());
    }

    private void insertBbiep(Bbiep bbiep, ScoreUser user, String topLevelAsbiepId) {
        if (bbiep == null) {
            throw new IllegalArgumentException();
        }
        if (bbiep.getBbiepId() != null) {
            return;
        }

        String userId = user.getUserId();

        BbiepRecord bbiepRecord = new BbiepRecord();
        bbiepRecord.setBbiepId(UUID.randomUUID().toString());
        bbiepRecord.setGuid(bbiep.getGuid());
        bbiepRecord.setBasedBccpManifestId(bbiep.getBasedBccpManifestId());
        bbiepRecord.setPath(bbiep.getPath());
        bbiepRecord.setHashPath(bbiep.getHashPath());
        bbiepRecord.setDefinition(bbiep.getDefinition());
        bbiepRecord.setRemark(bbiep.getRemark());
        bbiepRecord.setBizTerm(bbiep.getBizTerm());
        bbiepRecord.setCreatedBy(userId);
        bbiepRecord.setLastUpdatedBy(userId);
        bbiepRecord.setCreationTimestamp(LocalDateTime.now());
        bbiepRecord.setLastUpdateTimestamp(LocalDateTime.now());
        bbiepRecord.setOwnerTopLevelAsbiepId(topLevelAsbiepId);

        dslContext().insertInto(BBIEP)
                .set(bbiepRecord)
                .execute();
        bbiep.setBbiepId(bbiepRecord.getBbiepId());
    }

    private void insertAsbie(Asbie asbie, ScoreUser user, String topLevelAsbiepId) {
        if (asbie == null) {
            throw new IllegalArgumentException();
        }
        if (asbie.getAsbieId() != null) {
            return;
        }

        String userId = user.getUserId();

        AsbieRecord asbieRecord = new AsbieRecord();
        asbieRecord.setAsbieId(UUID.randomUUID().toString());
        asbieRecord.setGuid(asbie.getGuid());
        asbieRecord.setBasedAsccManifestId(asbie.getBasedAsccManifestId());
        asbieRecord.setPath(asbie.getPath());
        asbieRecord.setHashPath(asbie.getHashPath());
        asbieRecord.setFromAbieId(asbie.getFromAbieId());
        asbieRecord.setToAsbiepId(asbie.getToAsbiepId());
        asbieRecord.setDefinition(asbie.getDefinition());
        asbieRecord.setCardinalityMin(asbie.getCardinalityMin());
        asbieRecord.setCardinalityMax(asbie.getCardinalityMax());
        asbieRecord.setIsNillable((byte) (asbie.isNillable() ? 1 : 0));
        asbieRecord.setRemark(asbie.getRemark());
        asbieRecord.setIsUsed((byte) (asbie.isUsed() ? 1 : 0));
        asbieRecord.setSeqKey(BigDecimal.ZERO);
        asbieRecord.setCreatedBy(userId);
        asbieRecord.setLastUpdatedBy(userId);
        asbieRecord.setCreationTimestamp(LocalDateTime.now());
        asbieRecord.setLastUpdateTimestamp(LocalDateTime.now());
        asbieRecord.setOwnerTopLevelAsbiepId(topLevelAsbiepId);

        dslContext().insertInto(ASBIE)
                .set(asbieRecord)
                .execute();
        asbie.setAsbieId(asbieRecord.getAsbieId());
    }

    private void insertBbie(Bbie bbie, ScoreUser user, String topLevelAsbiepId) {
        if (bbie == null) {
            throw new IllegalArgumentException();
        }
        if (bbie.getBbieId() != null) {
            return;
        }

        String userId = user.getUserId();

        BbieRecord bbieRecord = new BbieRecord();
        bbieRecord.setBbieId(UUID.randomUUID().toString());
        bbieRecord.setGuid(bbie.getGuid());
        bbieRecord.setBasedBccManifestId(bbie.getBasedBccManifestId());
        bbieRecord.setPath(bbie.getPath());
        bbieRecord.setHashPath(bbie.getHashPath());
        bbieRecord.setFromAbieId(bbie.getFromAbieId());
        bbieRecord.setToBbiepId(bbie.getToBbiepId());
        if (bbie.getBdtPriRestriId() != null) {
            bbieRecord.setBdtPriRestriId(bbie.getBdtPriRestriId());
        }
        if (bbie.getCodeListId() != null) {
            bbieRecord.setCodeListId(bbie.getCodeListId());
        }
        if (bbie.getAgencyIdListId() != null) {
            bbieRecord.setAgencyIdListId(bbie.getAgencyIdListId());
        }
        bbieRecord.setDefaultValue(bbie.getDefaultValue());
        bbieRecord.setFixedValue(bbie.getFixedValue());
        bbieRecord.setDefinition(bbie.getDefinition());
        bbieRecord.setCardinalityMin(bbie.getCardinalityMin());
        bbieRecord.setCardinalityMax(bbie.getCardinalityMax());
        bbieRecord.setIsNillable((byte) (bbie.isNillable() ? 1 : 0));
        bbieRecord.setIsNull((byte) 0);
        bbieRecord.setRemark(bbie.getRemark());
        bbieRecord.setExample(bbie.getExample());
        bbieRecord.setIsUsed((byte) (bbie.isUsed() ? 1 : 0));
        bbieRecord.setSeqKey(BigDecimal.ZERO);
        bbieRecord.setCreatedBy(userId);
        bbieRecord.setLastUpdatedBy(userId);
        bbieRecord.setCreationTimestamp(LocalDateTime.now());
        bbieRecord.setLastUpdateTimestamp(LocalDateTime.now());
        bbieRecord.setOwnerTopLevelAsbiepId(topLevelAsbiepId);

        dslContext().insertInto(BBIE)
                .set(bbieRecord)
                .execute();
        bbie.setBbieId(bbieRecord.getBbieId());
    }

    private void insertBbieSc(BbieSc bbieSc, ScoreUser user, String topLevelAsbiepId) {
        if (bbieSc == null) {
            throw new IllegalArgumentException();
        }
        if (bbieSc.getBbieScId() != null) {
            return;
        }

        String userId = user.getUserId();

        BbieScRecord bbieScRecord = new BbieScRecord();
        bbieScRecord.setBbieScId(UUID.randomUUID().toString());
        bbieScRecord.setGuid(bbieSc.getGuid());
        bbieScRecord.setBasedDtScManifestId(bbieSc.getBasedDtScManifestId());
        bbieScRecord.setPath(bbieSc.getPath());
        bbieScRecord.setHashPath(bbieSc.getHashPath());
        bbieScRecord.setBbieId(bbieSc.getBbieId());
        if (bbieSc.getDtScPriRestriId() != null) {
            bbieScRecord.setDtScPriRestriId(bbieSc.getDtScPriRestriId());
        }
        if (bbieSc.getCodeListId() != null) {
            bbieScRecord.setCodeListId(bbieSc.getCodeListId());
        }
        if (bbieSc.getAgencyIdListId() != null) {
            bbieScRecord.setAgencyIdListId(bbieSc.getAgencyIdListId());
        }
        bbieScRecord.setDefaultValue(bbieSc.getDefaultValue());
        bbieScRecord.setFixedValue(bbieSc.getFixedValue());
        bbieScRecord.setDefinition(bbieSc.getDefinition());
        bbieScRecord.setCardinalityMin(bbieSc.getCardinalityMin());
        bbieScRecord.setCardinalityMax(bbieSc.getCardinalityMax());
        bbieScRecord.setBizTerm(bbieSc.getBizTerm());
        bbieScRecord.setRemark(bbieSc.getRemark());
        bbieScRecord.setExample(bbieSc.getExample());
        bbieScRecord.setIsUsed((byte) (bbieSc.isUsed() ? 1 : 0));
        bbieScRecord.setCreatedBy(userId);
        bbieScRecord.setLastUpdatedBy(userId);
        bbieScRecord.setCreationTimestamp(LocalDateTime.now());
        bbieScRecord.setLastUpdateTimestamp(LocalDateTime.now());
        bbieScRecord.setOwnerTopLevelAsbiepId(topLevelAsbiepId);

        dslContext().insertInto(BBIE_SC)
                .set(bbieScRecord)
                .execute();
        bbieSc.setBbieScId(bbieScRecord.getBbieScId());
    }

}
