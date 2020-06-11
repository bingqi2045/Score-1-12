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
import org.oagi.srt.repo.component.seqkey.SeqKeyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.ASCCP;
import static org.oagi.srt.entity.jooq.Tables.ASCCP_MANIFEST;
import static org.oagi.srt.entity.jooq.tables.Acc.ACC;
import static org.oagi.srt.entity.jooq.tables.AccManifest.ACC_MANIFEST;
import static org.oagi.srt.entity.jooq.tables.Ascc.ASCC;
import static org.oagi.srt.entity.jooq.tables.AsccManifest.ASCC_MANIFEST;

@Repository
public class AsccWriteRepository {

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
                .where(and(
                        ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                        ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(request.getAccManifestId()))
                ))
                .fetchOne();

        if (accManifestRecord == null) {
            throw new IllegalArgumentException("Source ACC does not exist.");
        }

        AsccpManifestRecord asccpManifestRecord = dslContext.selectFrom(ASCCP_MANIFEST)
                .where(and(
                        ASCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                        ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(request.getAsccpManifestId()))
                ))
                .fetchOne();

        if (asccpManifestRecord == null) {
            throw new IllegalArgumentException("Target ASCCP does not exist.");
        }

        if (dslContext.selectCount()
                .from(ASCC_MANIFEST)
                .where(and(
                        ASCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                        ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ULong.valueOf(request.getAccManifestId())),
                        ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ULong.valueOf(request.getAsccpManifestId()))
                ))
                .fetchOneInto(Integer.class) > 0) {
            throw new IllegalArgumentException("Target ASCCP has already included.");
        }

        if (dslContext.selectCount()
                .from(ASCCP_MANIFEST)
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .join(ASCC_MANIFEST).on(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID))
                .where(and(ASCCP.REUSABLE_INDICATOR.eq((byte) 0),
                        ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(request.getAsccpManifestId())))
                )
                .fetchOneInto(Integer.class) > 0) {
            throw new IllegalArgumentException("Target ASCCP is not reusable.");
        }

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();
        CcState accState = CcState.valueOf(accRecord.getState());
        if (accState != request.getInitialState()) {
            throw new IllegalArgumentException("The initial state of ASCC must be '" + accState + "'.");
        }

        AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId())).fetchOne();

        AsccRecord ascc = new AsccRecord();
        ascc.setGuid(SrtGuid.randomGuid());
        ascc.setDen(accRecord.getObjectClassTerm() + ". " + asccpRecord.getDen());
        ascc.setCardinalityMin(request.getCardinalityMin());
        ascc.setCardinalityMax(request.getCardinalityMax());
        ascc.setSeqKey(0); // @deprecated
        ascc.setFromAccId(accRecord.getAccId());
        ascc.setToAsccpId(asccpRecord.getAsccpId());
        ascc.setState(request.getInitialState().name());
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
        new SeqKeyHandler(dslContext, ascc).moveTo(request.getPos());

        AsccManifestRecord asccManifest = new AsccManifestRecord();
        asccManifest.setAsccId(ascc.getAsccId());
        asccManifest.setReleaseId(ULong.valueOf(request.getReleaseId()));
        asccManifest.setFromAccManifestId(accManifestRecord.getAccManifestId());
        asccManifest.setToAsccpManifestId(asccpManifestRecord.getAsccpManifestId());
        asccManifest.setAsccManifestId(
                dslContext.insertInto(ASCC_MANIFEST)
                        .set(asccManifest)
                        .returning(ASCC_MANIFEST.ASCC_MANIFEST_ID).fetchOne().getAsccManifestId()
        );

        upsertRevisionIntoAccAndAssociations(
                accRecord, accManifestRecord,
                ULong.valueOf(request.getReleaseId()),
                userId, timestamp
        );

        return new CreateAsccRepositoryResponse(asccManifest.getAsccManifestId().toBigInteger());
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

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(asccManifestRecord.getFromAccManifestId()))
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

        // update ascc record.
        asccRecord.setDen(accRecord.getObjectClassTerm() + ". " + dslContext.select(ASCCP.DEN)
                .from(ASCCP)
                .join(ASCCP_MANIFEST).on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(asccManifestRecord.getToAsccpManifestId()))
                .fetchOneInto(String.class)
        );
        asccRecord.setDefinition(request.getDefinition());
        asccRecord.setDefinitionSource(request.getDefinitionSource());
        asccRecord.setIsDeprecated((byte) ((request.isDeprecated()) ? 1 : 0));
        asccRecord.setCardinalityMin(request.getCardinalityMin());
        asccRecord.setCardinalityMax(request.getCardinalityMax());
        asccRecord.setLastUpdatedBy(userId);
        asccRecord.setLastUpdateTimestamp(timestamp);
        asccRecord.update(ASCC.DEN,
                ASCC.DEFINITION, ASCC.DEFINITION_SOURCE,
                ASCC.CARDINALITY_MIN, ASCC.CARDINALITY_MAX,
                ASCC.IS_DEPRECATED,
                ASCC.LAST_UPDATED_BY, ASCC.LAST_UPDATE_TIMESTAMP);

        upsertRevisionIntoAccAndAssociations(
                accRecord, accManifestRecord,
                accManifestRecord.getReleaseId(),
                userId, timestamp
        );

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

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(asccManifestRecord.getFromAccManifestId()))
                .fetchOne();

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(accRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be deleted.");
        }

        if (!accRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update acc state.
        asccRecord.setState(CcState.Deleted.name());
        asccRecord.setLastUpdatedBy(userId);
        asccRecord.setLastUpdateTimestamp(timestamp);
        asccRecord.update(ASCC.STATE,
                ASCC.LAST_UPDATED_BY, ASCC.LAST_UPDATE_TIMESTAMP);

        upsertRevisionIntoAccAndAssociations(
                accRecord, accManifestRecord,
                accManifestRecord.getReleaseId(),
                userId, timestamp
        );

        return new DeleteAsccRepositoryResponse(asccManifestRecord.getAsccManifestId().toBigInteger());
    }
}
