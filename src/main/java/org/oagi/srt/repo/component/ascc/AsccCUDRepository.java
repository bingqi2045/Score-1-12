package org.oagi.srt.repo.component.ascc;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.RevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.max;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.entity.jooq.tables.Acc.ACC;
import static org.oagi.srt.entity.jooq.tables.AccManifest.ACC_MANIFEST;
import static org.oagi.srt.entity.jooq.tables.Ascc.ASCC;
import static org.oagi.srt.entity.jooq.tables.AsccManifest.ASCC_MANIFEST;

@Repository
public class AsccCUDRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RevisionRepository revisionRepository;

    public CreateAsccRepositoryResponse createAscc(CreateAsccRepositoryRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(request.getUser()));
        LocalDateTime timestamp = request.getLocalDateTime();

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(request.getAccManifestId()))).fetchOne();

        if (accManifestRecord == null) {
            throw new IllegalArgumentException("ACC Manifest(" + request.getAccManifestId() + ") dose NOT exist.");
        }

        AsccpManifestRecord asccpManifestRecord = dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(request.getAsccpManifestId()))).fetchOne();

        if (asccpManifestRecord == null) {
            throw new IllegalArgumentException("ASCCP Manifest(" + request.getAsccpManifestId() + ") dose NOT exist.");
        }

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();

        AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId())).fetchOne();

        AsccRecord ascc = new AsccRecord();
        ascc.setGuid(SrtGuid.randomGuid());
        ascc.setDen(accRecord.getObjectClassTerm() + ". " + asccpRecord.getPropertyTerm());
        ascc.setCardinalityMin(0);
        ascc.setCardinalityMax(-1);
        ascc.setSeqKey(getNextSeqKey(accManifestRecord.getAccManifestId()));
        ascc.setFromAccId(accRecord.getAccId());
        ascc.setToAsccpId(asccpRecord.getAsccpId());
        ascc.setState(CcState.WIP.name());
        ascc.setIsDeprecated((byte) 0);
        ascc.setCreatedBy(userId);
        ascc.setLastUpdatedBy(userId);
        ascc.setOwnerUserId(userId);
        ascc.setCreationTimestamp(timestamp);
        ascc.setLastUpdateTimestamp(timestamp);

        ascc.setAsccId(
                dslContext.insertInto(ASCC)
                        .set(ascc)
                        .returning(ASCC.ASCC_ID).fetchOne().getAsccId()
        );

        AsccManifestRecord asccManifest = new AsccManifestRecord();
        asccManifest.setAsccId(ascc.getAsccId());
        asccManifest.setReleaseId(ULong.valueOf(request.getReleaseId()));
        asccManifest.setFromAccManifestId(accManifestRecord.getAccManifestId());
        asccManifest.setToAsccpManifestId(asccpManifestRecord.getAsccpManifestId());

        RevisionRecord revisionRecord =
                revisionRepository.insertAccRevision(
                        accRecord,
                        accManifestRecord.getRevisionId(),
                        RevisionAction.AssociateModified,
                        userId, timestamp);

        // Update Acc revision Id too
        accManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        accManifestRecord.update(ACC_MANIFEST.REVISION_ID);
        asccManifest.setRevisionId(revisionRecord.getRevisionId());

        asccManifest.setAsccManifestId(
                dslContext.insertInto(ASCC_MANIFEST)
                        .set(asccManifest)
                        .returning(ASCC_MANIFEST.ASCC_MANIFEST_ID).fetchOne().getAsccManifestId()
        );



        return new CreateAsccRepositoryResponse(asccManifest.getAsccManifestId().toBigInteger());
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

        return Math.max(asccMaxSeqKey, bccMaxSeqKey) + 1;
    }

    public UpdateAsccPropertiesRepositoryResponse updateAsccProperties(UpdateAsccPropertiesRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        AsccManifestRecord asccManifestRecord = dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(
                        ULong.valueOf(request.getAsccManifestId())
                ))
                .fetchOne();

        AsccRecord asccRecord = dslContext.selectFrom(ASCC)
                .where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(asccRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
        }

        if (!asccRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update ascc record.
        asccRecord.setDefinition(request.getDefinition());
        asccRecord.setDefinitionSource(request.getDefinitionSource());
        asccRecord.setIsDeprecated((byte) ((request.isDeprecated()) ? 1 : 0));
        asccRecord.setCardinalityMin(request.getCardinalityMin());
        asccRecord.setCardinalityMax(request.getCardinalityMax());
        asccRecord.setLastUpdatedBy(userId);
        asccRecord.setLastUpdateTimestamp(timestamp);
        asccRecord.update(ASCC.DEFINITION, ASCC.DEFINITION_SOURCE,
                ASCC.CARDINALITY_MIN, ASCC.CARDINALITY_MAX,
                ACC.IS_DEPRECATED,
                ACC.LAST_UPDATED_BY, ACC.LAST_UPDATE_TIMESTAMP);

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(asccManifestRecord.getFromAccManifestId())).fetchOne();

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

        asccManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        asccManifestRecord.update(ASCC_MANIFEST.REVISION_ID);

        return new UpdateAsccPropertiesRepositoryResponse(asccManifestRecord.getAsccManifestId().toBigInteger());
    }

    public DeleteAsccRepositoryResponse deleteAscc(DeleteAsccRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        AsccManifestRecord asccManifestRecord = dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(
                        ULong.valueOf(request.getAsccManifestId())
                ))
                .fetchOne();

        AsccRecord asccRecord = dslContext.selectFrom(ASCC)
                .where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(asccRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be deleted.");
        }

        if (!asccRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update acc state.
        asccRecord.setState(CcState.Deleted.name());
        asccRecord.setLastUpdatedBy(userId);
        asccRecord.setLastUpdateTimestamp(timestamp);
        asccRecord.update(ASCC.STATE,
                ASCC.LAST_UPDATED_BY, ASCC.LAST_UPDATE_TIMESTAMP);

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(asccManifestRecord.getFromAccManifestId())).fetchOne();

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

        asccManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        asccManifestRecord.update(ASCC_MANIFEST.REVISION_ID);

        return new DeleteAsccRepositoryResponse(asccManifestRecord.getAsccManifestId().toBigInteger());
    }
}
