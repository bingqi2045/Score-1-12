package org.oagi.score.repo.component.bcc;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.data.AppUser;
import org.oagi.score.data.BCCEntityType;
import org.oagi.score.data.RevisionAction;
import org.oagi.score.entity.jooq.tables.records.*;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.helper.SrtGuid;
import org.oagi.score.repo.RevisionRepository;
import org.oagi.score.repo.component.seqkey.SeqKeyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.data.BCCEntityType.Attribute;
import static org.oagi.score.data.BCCEntityType.Element;
import static org.oagi.score.entity.jooq.Tables.*;
import static org.oagi.score.entity.jooq.tables.Acc.ACC;
import static org.oagi.score.entity.jooq.tables.AccManifest.ACC_MANIFEST;
import static org.oagi.score.entity.jooq.tables.Bcc.BCC;
import static org.oagi.score.entity.jooq.tables.BccManifest.BCC_MANIFEST;
import static org.oagi.score.repo.component.seqkey.MoveTo.LAST;
import static org.oagi.score.repo.component.seqkey.MoveTo.LAST_OF_ATTR;

@Repository
public class BccWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RevisionRepository revisionRepository;

    public CreateBccRepositoryResponse createBcc(CreateBccRepositoryRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(request.getUser()));
        LocalDateTime timestamp = request.getLocalDateTime();

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(request.getAccManifestId())))
                .fetchOne();

        if (accManifestRecord == null) {
            throw new IllegalArgumentException("Source ACC does not exist.");
        }

        BccpManifestRecord bccpManifestRecord = dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(request.getBccpManifestId())))
                .fetchOne();

        if (bccpManifestRecord == null) {
            throw new IllegalArgumentException("Target BCCP does not exist.");
        }

        if (dslContext.selectCount()
                .from(BCC_MANIFEST)
                .join(BCC).on(BCC_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                .where(and(
                        BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                        BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ULong.valueOf(request.getAccManifestId())),
                        BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(ULong.valueOf(request.getBccpManifestId())),
                        BCC.STATE.notEqual(CcState.Deleted.name())
                ))
                .fetchOneInto(Integer.class) > 0) {
            throw new IllegalArgumentException("Target BCCP has already included.");
        }

        if(accManifestRecord.getBasedAccManifestId() != null &&
                dslContext.selectCount()
                        .from(BCC_MANIFEST)
                        .join(BCC).on(BCC_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                        .where(and(
                                BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getBasedAccManifestId()),
                                BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(ULong.valueOf(request.getBccpManifestId())),
                                BCC.STATE.notEqual(CcState.Deleted.name())
                        ))
                        .fetchOneInto(Integer.class) > 0) {
            throw new IllegalArgumentException("Target BCCP has already included on based ACC.");
        }

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();
        CcState accState = CcState.valueOf(accRecord.getState());
        if (accState != request.getInitialState()) {
            throw new IllegalArgumentException("The initial state of BCC must be '" + accState + "'.");
        }

        BccpRecord bccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId())).fetchOne();

        BccRecord bcc = new BccRecord();
        bcc.setGuid(SrtGuid.randomGuid());
        bcc.setDen(accRecord.getObjectClassTerm() + ". " + bccpRecord.getDen());
        bcc.setCardinalityMin(0);
        bcc.setCardinalityMax(-1);
        bcc.setSeqKey(0); // @deprecated
        bcc.setFromAccId(accRecord.getAccId());
        bcc.setToBccpId(bccpRecord.getBccpId());
        bcc.setEntityType(Element.getValue());
        bcc.setState(CcState.WIP.name());
        bcc.setIsDeprecated((byte) 0);
        bcc.setIsNillable((byte) 0);
        bcc.setCreatedBy(userId);
        bcc.setLastUpdatedBy(userId);
        bcc.setOwnerUserId(userId);
        bcc.setCreationTimestamp(timestamp);
        bcc.setLastUpdateTimestamp(timestamp);
        bcc.setBccId(
                dslContext.insertInto(BCC)
                        .set(bcc)
                        .returning(BCC.BCC_ID).fetchOne().getBccId()
        );
        new SeqKeyHandler(dslContext, bcc).moveTo(request.getPos());

        BccManifestRecord bccManifestRecord = new BccManifestRecord();
        bccManifestRecord.setBccId(bcc.getBccId());
        bccManifestRecord.setReleaseId(ULong.valueOf(request.getReleaseId()));
        bccManifestRecord.setFromAccManifestId(accManifestRecord.getAccManifestId());
        bccManifestRecord.setToBccpManifestId(bccpManifestRecord.getBccpManifestId());
        bccManifestRecord.setBccManifestId(
                dslContext.insertInto(BCC_MANIFEST)
                        .set(bccManifestRecord)
                        .returning(BCC_MANIFEST.BCC_MANIFEST_ID).fetchOne().getBccManifestId()
        );

        upsertRevisionIntoAccAndAssociations(
                accRecord, accManifestRecord,
                ULong.valueOf(request.getReleaseId()),
                userId, timestamp
        );

        return new CreateBccRepositoryResponse(bccManifestRecord.getBccManifestId().toBigInteger());
    }

    private void upsertRevisionIntoAccAndAssociations(AccRecord accRecord,
                                                      AccManifestRecord accManifestRecord,
                                                      ULong releaseId,
                                                      ULong userId, LocalDateTime timestamp) {
        RevisionRecord revisionRecord =
                revisionRepository.insertAccRevision(
                        accRecord,
                        accManifestRecord.getRevisionId(),
                        RevisionAction.Modified,
                        userId, timestamp);

        accManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        accManifestRecord.update(ACC_MANIFEST.REVISION_ID);
    }

    public UpdateBccPropertiesRepositoryResponse updateBccProperties(UpdateBccPropertiesRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        BccManifestRecord bccManifestRecord = dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(
                        ULong.valueOf(request.getBccManifestId())
                ))
                .fetchOne();

        BccRecord bccRecord = dslContext.selectFrom(BCC)
                .where(BCC.BCC_ID.eq(bccManifestRecord.getBccId()))
                .fetchOne();

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(bccManifestRecord.getFromAccManifestId()))
                .fetchOne();

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(accRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
        }

        if (!accRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update bcc record.
        bccRecord.setDen(accRecord.getObjectClassTerm() + ". " + dslContext.select(BCCP.DEN)
                .from(BCCP)
                .join(BCCP_MANIFEST).on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_MANIFEST_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(bccManifestRecord.getToBccpManifestId()))
                .fetchOneInto(String.class)
        );
        bccRecord.setDefinition(request.getDefinition());
        bccRecord.setDefinitionSource(request.getDefinitionSource());
        if (request.getEntityType().getValue() != bccRecord.getEntityType()) {
            bccRecord.setEntityType(request.getEntityType().getValue());
            if (request.getEntityType() == Element) {
                new SeqKeyHandler(dslContext, bccRecord).moveTo(LAST);
            } else if (request.getEntityType() == Attribute) {
                new SeqKeyHandler(dslContext, bccRecord).moveTo(LAST_OF_ATTR);
            }
        }
        bccRecord.setIsDeprecated((byte) (request.isDeprecated() ? 1 : 0));
        bccRecord.setIsNillable((byte) (request.isNillable() ? 1 : 0));
        bccRecord.setCardinalityMin(request.getCardinalityMin());
        bccRecord.setCardinalityMax(request.getCardinalityMax());
        bccRecord.setDefaultValue(request.getDefaultValue());
        bccRecord.setFixedValue(request.getFixedValue());
        bccRecord.setLastUpdatedBy(userId);
        bccRecord.setLastUpdateTimestamp(timestamp);
        bccRecord.update(BCC.DEN,
                BCC.DEFINITION, BCC.DEFINITION_SOURCE,
                BCC.CARDINALITY_MIN, BCC.CARDINALITY_MAX,
                BCC.ENTITY_TYPE, BCC.IS_DEPRECATED, BCC.IS_NILLABLE,
                BCC.DEFAULT_VALUE, BCC.FIXED_VALUE,
                BCC.LAST_UPDATED_BY, BCC.LAST_UPDATE_TIMESTAMP);

        upsertRevisionIntoAccAndAssociations(
                accRecord, accManifestRecord,
                accManifestRecord.getReleaseId(),
                userId, timestamp
        );

        return new UpdateBccPropertiesRepositoryResponse(bccManifestRecord.getBccManifestId().toBigInteger());
    }

    private void setBccSeqKeyByEntityType(BccRecord bccRecord,
                                          BCCEntityType source, BCCEntityType target) {

        dslContext.selectFrom(SEQ_KEY)
                .where(SEQ_KEY.FROM_ACC_ID.eq(bccRecord.getFromAccId()))
                .fetch();

        SeqKeyRecord thisSeqKeyRecord = dslContext.selectFrom(SEQ_KEY)
                .where(SEQ_KEY.SEQ_KEY_ID.eq(bccRecord.getSeqKeyId()))
                .fetchOne();
        SeqKeyRecord prevSeqKeyRecord = (thisSeqKeyRecord.getPrevSeqKeyId() != null) ?
                dslContext.selectFrom(SEQ_KEY)
                        .where(SEQ_KEY.SEQ_KEY_ID.eq(thisSeqKeyRecord.getPrevSeqKeyId()))
                        .fetchOne() : null;
        SeqKeyRecord nextSeqKeyRecord = (thisSeqKeyRecord.getNextSeqKeyId() != null) ?
                dslContext.selectFrom(SEQ_KEY)
                        .where(SEQ_KEY.SEQ_KEY_ID.eq(thisSeqKeyRecord.getNextSeqKeyId()))
                        .fetchOne() : null;

        if (source == Element && target == Attribute) {

        } else if (source == Attribute && target == Element) {

        }
    }

    public DeleteBccRepositoryResponse deleteBcc(DeleteBccRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        BccManifestRecord bccManifestRecord = dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(
                        ULong.valueOf(request.getBccManifestId())
                ))
                .fetchOne();

        BccRecord bccRecord = dslContext.selectFrom(BCC)
                .where(BCC.BCC_ID.eq(bccManifestRecord.getBccId()))
                .fetchOne();

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(bccManifestRecord.getFromAccManifestId())).fetchOne();

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(accRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be deleted.");
        }

        if (!accRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update acc state.
        bccRecord.setState(CcState.Deleted.name());
        bccRecord.setLastUpdatedBy(userId);
        bccRecord.setLastUpdateTimestamp(timestamp);
        bccRecord.update(BCC.STATE,
                BCC.LAST_UPDATED_BY, BCC.LAST_UPDATE_TIMESTAMP);

        upsertRevisionIntoAccAndAssociations(
                accRecord, accManifestRecord,
                accManifestRecord.getReleaseId(),
                userId, timestamp
        );

        return new DeleteBccRepositoryResponse(bccManifestRecord.getBccManifestId().toBigInteger());
    }
}
