package org.oagi.srt.repo.component.bcc;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.RevisionRepository;
import org.oagi.srt.repo.component.bcc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.max;
import static org.oagi.srt.data.BCCEntityType.Element;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.entity.jooq.tables.Acc.ACC;
import static org.oagi.srt.entity.jooq.tables.AccManifest.ACC_MANIFEST;
import static org.oagi.srt.entity.jooq.tables.Bcc.BCC;
import static org.oagi.srt.entity.jooq.tables.BccManifest.BCC_MANIFEST;

@Repository
public class BccCUDRepository {

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
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(request.getAccManifestId()))).fetchOne();

        if (accManifestRecord == null) {
            throw new IllegalArgumentException("ACC Manifest(" + request.getAccManifestId() + ") dose NOT exist.");
        }

        BccpManifestRecord bccpManifestRecord = dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(request.getBccpManifestId()))).fetchOne();

        if (bccpManifestRecord == null) {
            throw new IllegalArgumentException("BCCP Manifest(" + request.getBccpManifestId() + ") dose NOT exist.");
        }

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();

        BccpRecord bccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId())).fetchOne();

        BccRecord bcc = new BccRecord();
        bcc.setGuid(SrtGuid.randomGuid());
        bcc.setDen(accRecord.getObjectClassTerm() + ". " + bccpRecord.getPropertyTerm());
        bcc.setCardinalityMin(0);
        bcc.setCardinalityMax(-1);
        bcc.setSeqKey(getNextSeqKey(accManifestRecord.getAccManifestId()));
        bcc.setFromAccId(accRecord.getAccId());
        bcc.setToBccpId(bccpRecord.getBccpId());
        bcc.setEntityType(Element.getValue());
        bcc.setState(CcState.WIP.name());
        bcc.setIsDeprecated((byte) 0);
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

        BccManifestRecord bccManifest = new BccManifestRecord();
        bccManifest.setBccId(bcc.getBccId());
        bccManifest.setReleaseId(ULong.valueOf(request.getReleaseId()));
        bccManifest.setFromAccManifestId(accManifestRecord.getAccManifestId());
        bccManifest.setToBccpManifestId(bccpManifestRecord.getBccpManifestId());

        RevisionRecord revisionRecord =
                revisionRepository.insertAccRevision(
                        accRecord,
                        accManifestRecord.getRevisionId(),
                        RevisionAction.AssociateModified,
                        userId, timestamp);

        // Update Acc revision Id too
        accManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        accManifestRecord.update(ACC_MANIFEST.REVISION_ID);
        bccManifest.setRevisionId(revisionRecord.getRevisionId());

        bccManifest.setBccManifestId(
                dslContext.insertInto(BCC_MANIFEST)
                        .set(bccManifest)
                        .returning(BCC_MANIFEST.BCC_MANIFEST_ID).fetchOne().getBccManifestId()
        );



        return new CreateBccRepositoryResponse(bccManifest.getBccManifestId().toBigInteger());
    }

    private Integer getNextSeqKey(ULong accManifestId) {
        if (accManifestId == null || accManifestId.longValue() <= 0L) {
            return null;
        }

        Integer asccMaxSeqKey = dslContext.select(max(ASCC.SEQ_KEY))
                .from(ASCC)
                .join(ASCC_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
                .where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestId))
                .fetchOneInto(Integer.class);
        if (asccMaxSeqKey == null) {
            asccMaxSeqKey = 0;
        }

        Integer bccMaxSeqKey = dslContext.select(max(BCC.SEQ_KEY))
                .from(BCC)
                .join(BCC_MANIFEST)
                .on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                .where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestId))
                .fetchOneInto(Integer.class);

        if (bccMaxSeqKey == null) {
            bccMaxSeqKey = 0;
        }

        return Math.max(bccMaxSeqKey, bccMaxSeqKey) + 1;
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

        if (!CcState.WIP.equals(CcState.valueOf(bccRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
        }

        if (!bccRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update bcc record.
        bccRecord.setDefinition(request.getDefinition());
        bccRecord.setDefinitionSource(request.getDefinitionSource());
        bccRecord.setIsDeprecated((byte) ((request.isDeprecated()) ? 1 : 0));
        bccRecord.setCardinalityMin(request.getCardinalityMin());
        bccRecord.setCardinalityMax(request.getCardinalityMax());
        bccRecord.setLastUpdatedBy(userId);
        bccRecord.setLastUpdateTimestamp(timestamp);
        bccRecord.update(BCC.DEFINITION, BCC.DEFINITION_SOURCE,
                BCC.CARDINALITY_MIN, BCC.CARDINALITY_MAX,
                ACC.IS_DEPRECATED,
                ACC.LAST_UPDATED_BY, ACC.LAST_UPDATE_TIMESTAMP);

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(bccManifestRecord.getFromAccManifestId())).fetchOne();

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();

        // creates new revision for updated record.
        RevisionRecord revisionRecord =
                revisionRepository.insertAccRevision(
                        accRecord, accManifestRecord.getRevisionId(),
                        RevisionAction.AssociateModified,
                        userId, timestamp);

        // Update Acc RevisionId too.
        accManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        accManifestRecord.update(ACC_MANIFEST.REVISION_ID);

        bccManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        bccManifestRecord.update(BCC_MANIFEST.REVISION_ID);

        return new UpdateBccPropertiesRepositoryResponse(bccManifestRecord.getBccManifestId().toBigInteger());
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

        if (!CcState.WIP.equals(CcState.valueOf(bccRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be deleted.");
        }

        if (!bccRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update acc state.
        bccRecord.setState(CcState.Deleted.name());
        bccRecord.setLastUpdatedBy(userId);
        bccRecord.setLastUpdateTimestamp(timestamp);
        bccRecord.update(BCC.STATE,
                BCC.LAST_UPDATED_BY, BCC.LAST_UPDATE_TIMESTAMP);

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(bccManifestRecord.getFromAccManifestId())).fetchOne();

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();

        // creates new revision for deleted record.
        RevisionRecord revisionRecord =
                revisionRepository.insertAccRevision(
                        accRecord, accManifestRecord.getRevisionId(),
                        RevisionAction.AssociateModified,
                        userId, timestamp);

        // Update Acc revisionId too.
        accManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        accManifestRecord.update(ACC_MANIFEST.REVISION_ID);

        bccManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        bccManifestRecord.update(BCC_MANIFEST.REVISION_ID);

        return new DeleteBccRepositoryResponse(bccManifestRecord.getBccManifestId().toBigInteger());
    }
}
