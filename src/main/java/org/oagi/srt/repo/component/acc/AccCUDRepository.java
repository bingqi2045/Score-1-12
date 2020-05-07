package org.oagi.srt.repo.component.acc;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.RevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.entity.jooq.tables.Acc.ACC;
import static org.oagi.srt.entity.jooq.tables.AccManifest.ACC_MANIFEST;
import static org.oagi.srt.entity.jooq.tables.BccpManifest.BCCP_MANIFEST;

@Repository
public class AccCUDRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RevisionRepository revisionRepository;

    private String objectClassTerm(ULong accId) {
        return dslContext.select(ACC.OBJECT_CLASS_TERM).from(ACC)
                .where(ACC.ACC_ID.eq(accId))
                .fetchOneInto(String.class);
    }

    public CreateAccRepositoryResponse createAcc(CreateAccRepositoryRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(request.getUser()));
        LocalDateTime timestamp = request.getLocalDateTime();

        AccRecord acc = new AccRecord();
        acc.setGuid(SrtGuid.randomGuid());
        acc.setObjectClassTerm(request.getInitialObjectClassTerm());
        acc.setDen(acc.getObjectClassTerm() + ". Details");
        acc.setOagisComponentType(OagisComponentType.Semantics.getValue());
        acc.setState(CcState.WIP.name());
        acc.setIsAbstract((byte) 0);
        acc.setIsDeprecated((byte) 0);
        acc.setNamespaceId(null);
        acc.setCreatedBy(userId);
        acc.setLastUpdatedBy(userId);
        acc.setOwnerUserId(userId);
        acc.setCreationTimestamp(timestamp);
        acc.setLastUpdateTimestamp(timestamp);

        acc.setAccId(
                dslContext.insertInto(ACC)
                        .set(acc)
                        .returning(ACC.ACC_ID).fetchOne().getAccId()
        );

        AccManifestRecord accManifest = new AccManifestRecord();
        accManifest.setAccId(acc.getAccId());
        accManifest.setReleaseId(ULong.valueOf(request.getReleaseId()));

        RevisionRecord revisionRecord =
                revisionRepository.insertAccRevision(
                        acc,
                        RevisionAction.Added,
                        userId, timestamp);
        accManifest.setRevisionId(revisionRecord.getRevisionId());

        accManifest.setAccManifestId(
                dslContext.insertInto(ACC_MANIFEST)
                        .set(accManifest)
                        .returning(ACC_MANIFEST.ACC_MANIFEST_ID).fetchOne().getAccManifestId()
        );

        return new CreateAccRepositoryResponse(accManifest.getAccManifestId().toBigInteger());
    }

    public ReviseAccRepositoryResponse reviseAcc(ReviseAccRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(
                        ULong.valueOf(request.getAccManifestId())
                ))
                .fetchOne();

        AccRecord prevAccRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId()))
                .fetchOne();

        if (!CcState.Published.equals(CcState.valueOf(prevAccRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'Published' state can be revised.");
        }

        ULong workingReleaseId = dslContext.select(RELEASE.RELEASE_ID)
                .from(RELEASE)
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchOneInto(ULong.class);

        ULong targetReleaseId = accManifestRecord.getReleaseId();
        if (user.isDeveloper()) {
            if (!targetReleaseId.equals(workingReleaseId)) {
                throw new IllegalArgumentException("It only allows to revise the component in 'Working' branch for developers.");
            }
        } else {
            if (targetReleaseId.equals(workingReleaseId)) {
                throw new IllegalArgumentException("It only allows to revise the component in non-'Working' branch for end-users.");
            }
        }

        boolean ownerIsDeveloper = dslContext.select(APP_USER.IS_DEVELOPER)
                .from(APP_USER)
                .where(APP_USER.APP_USER_ID.eq(prevAccRecord.getOwnerUserId()))
                .fetchOneInto(Boolean.class);

        if (user.isDeveloper() != ownerIsDeveloper) {
            throw new IllegalArgumentException("It only allows to revise the component for users in the same roles.");
        }

        // creates new acc for revised record.
        AccRecord nextAccRecord = prevAccRecord.copy();
        nextAccRecord.setState(CcState.WIP.name());
        nextAccRecord.setCreatedBy(userId);
        nextAccRecord.setLastUpdatedBy(userId);
        nextAccRecord.setOwnerUserId(userId);
        nextAccRecord.setCreationTimestamp(timestamp);
        nextAccRecord.setLastUpdateTimestamp(timestamp);
        nextAccRecord.setPrevAccId(prevAccRecord.getAccId());
        nextAccRecord.setAccId(
                dslContext.insertInto(ACC)
                        .set(nextAccRecord)
                        .returning(ACC.ACC_ID).fetchOne().getAccId()
        );

        prevAccRecord.setNextAccId(nextAccRecord.getAccId());
        prevAccRecord.update(ACC.NEXT_ACC_ID);

        // create new associations for revised record.
        createNewAsccListForRevisedRecord(accManifestRecord, nextAccRecord, targetReleaseId, userId, timestamp);
        createNewBccListForRevisedRecord(accManifestRecord, nextAccRecord, targetReleaseId, userId, timestamp);

        // creates new revision for revised record.
        RevisionRecord revisionRecord =
                revisionRepository.insertAccRevision(
                        nextAccRecord, accManifestRecord.getRevisionId(),
                        RevisionAction.Revised,
                        userId, timestamp);

        ULong responseAccManifestId;
        // Developers only work for 'Working' branch whose manifest records are initially generated by system,
        // so it doesn't need to create new manifest.
        if (user.isDeveloper()) {
            accManifestRecord.setAccId(nextAccRecord.getAccId());
            accManifestRecord.setRevisionId(revisionRecord.getRevisionId());
            accManifestRecord.update(ACC_MANIFEST.ACC_ID, ACC_MANIFEST.REVISION_ID);

            responseAccManifestId = accManifestRecord.getAccManifestId();
        } else {
            // creates new acc manifest for revised record.
            AccManifestRecord nextAccManifestRecord = accManifestRecord.copy();
            nextAccManifestRecord.setAccId(nextAccRecord.getAccId());
            nextAccManifestRecord.setReleaseId(targetReleaseId);
            nextAccManifestRecord.setRevisionId(revisionRecord.getRevisionId());
            nextAccManifestRecord.setPrevAccManifestId(accManifestRecord.getAccManifestId());
            nextAccManifestRecord.setAccManifestId(
                    dslContext.insertInto(ACC_MANIFEST)
                            .set(nextAccManifestRecord)
                            .returning(ACC_MANIFEST.ACC_MANIFEST_ID).fetchOne().getAccManifestId()
            );

            accManifestRecord.setNextAccManifestId(nextAccManifestRecord.getAccManifestId());
            accManifestRecord.update(ACC_MANIFEST.NEXT_ACC_MANIFEST_ID);

            responseAccManifestId = nextAccManifestRecord.getAccManifestId();
        }

        // update `conflict` for asccp_manifests' role_of_acc_manifest_id which indicates given acc manifest.
        dslContext.update(ASCCP_MANIFEST)
                .set(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID, responseAccManifestId)
                .set(ASCCP_MANIFEST.CONFLICT, (byte) 1)
                .where(and(
                        ASCCP_MANIFEST.RELEASE_ID.eq(targetReleaseId),
                        ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.in(Arrays.asList(
                                accManifestRecord.getAccManifestId(),
                                accManifestRecord.getPrevAccManifestId()))
                ))
                .execute();

        // update `conflict` for acc_manifests' based_acc_manifest_id which indicates given acc manifest.
        dslContext.update(ACC_MANIFEST)
                .set(ACC_MANIFEST.BASED_ACC_MANIFEST_ID, responseAccManifestId)
                .set(ACC_MANIFEST.CONFLICT, (byte) 1)
                .where(and(
                        ACC_MANIFEST.RELEASE_ID.eq(targetReleaseId),
                        ACC_MANIFEST.BASED_ACC_MANIFEST_ID.in(Arrays.asList(
                                accManifestRecord.getAccManifestId(),
                                accManifestRecord.getPrevAccManifestId()))
                ))
                .execute();

        return new ReviseAccRepositoryResponse(responseAccManifestId.toBigInteger());
    }

    private void createNewAsccListForRevisedRecord(
            AccManifestRecord accManifestRecord,
            AccRecord nextAccRecord,
            ULong targetReleaseId,
            ULong userId,
            LocalDateTime timestamp) {
        for (AsccManifestRecord asccManifestRecord : dslContext.selectFrom(ASCC_MANIFEST)
                .where(and(
                        ASCC_MANIFEST.RELEASE_ID.eq(targetReleaseId),
                        ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getPrevAccManifestId())
                ))
                .fetch()) {

            AsccRecord prevAsccRecord = dslContext.selectFrom(ASCC)
                    .where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId()))
                    .fetchOne();

            AsccRecord nextAsccRecord = prevAsccRecord.copy();
            nextAsccRecord.setFromAccId(nextAccRecord.getAccId());
            nextAsccRecord.setToAsccpId(
                    dslContext.select(ASCCP_MANIFEST.ASCCP_ID)
                            .from(ASCCP_MANIFEST)
                            .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(asccManifestRecord.getToAsccpManifestId()))
                            .fetchOneInto(ULong.class)
            );
            nextAsccRecord.setState(CcState.WIP.name());
            nextAsccRecord.setCreatedBy(userId);
            nextAsccRecord.setLastUpdatedBy(userId);
            nextAsccRecord.setOwnerUserId(userId);
            nextAsccRecord.setCreationTimestamp(timestamp);
            nextAsccRecord.setLastUpdateTimestamp(timestamp);
            nextAsccRecord.setPrevAsccId(prevAsccRecord.getAsccId());
            nextAsccRecord.setAsccId(
                    dslContext.insertInto(ASCC)
                            .set(nextAsccRecord)
                            .returning(ASCC.ASCC_ID).fetchOne().getAsccId()
            );

            prevAsccRecord.setNextAsccId(nextAsccRecord.getAsccId());
            prevAsccRecord.update(ASCC.NEXT_ASCC_ID);

            asccManifestRecord.setAsccId(nextAsccRecord.getAsccId());
            asccManifestRecord.setFromAccManifestId(accManifestRecord.getAccManifestId());
            asccManifestRecord.update(ASCC_MANIFEST.ASCC_ID, ASCC_MANIFEST.FROM_ACC_MANIFEST_ID);
        }
    }

    private void createNewBccListForRevisedRecord(
            AccManifestRecord accManifestRecord,
            AccRecord nextAccRecord,
            ULong targetReleaseId,
            ULong userId,
            LocalDateTime timestamp) {
        for (BccManifestRecord bccManifestRecord : dslContext.selectFrom(BCC_MANIFEST)
                .where(and(
                        BCC_MANIFEST.RELEASE_ID.eq(targetReleaseId),
                        BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getPrevAccManifestId())
                ))
                .fetch()) {

            BccRecord prevBccRecord = dslContext.selectFrom(BCC)
                    .where(BCC.BCC_ID.eq(bccManifestRecord.getBccId()))
                    .fetchOne();

            BccRecord nextBccRecord = prevBccRecord.copy();
            nextBccRecord.setFromAccId(nextAccRecord.getAccId());
            nextBccRecord.setToBccpId(
                    dslContext.select(BCCP_MANIFEST.BCCP_ID)
                            .from(BCCP_MANIFEST)
                            .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(bccManifestRecord.getToBccpManifestId()))
                            .fetchOneInto(ULong.class)
            );
            nextBccRecord.setState(CcState.WIP.name());
            nextBccRecord.setCreatedBy(userId);
            nextBccRecord.setLastUpdatedBy(userId);
            nextBccRecord.setOwnerUserId(userId);
            nextBccRecord.setCreationTimestamp(timestamp);
            nextBccRecord.setLastUpdateTimestamp(timestamp);
            nextBccRecord.setPrevBccId(prevBccRecord.getBccId());
            nextBccRecord.setBccId(
                    dslContext.insertInto(BCC)
                            .set(nextBccRecord)
                            .returning(BCC.BCC_ID).fetchOne().getBccId()
            );

            prevBccRecord.setNextBccId(nextBccRecord.getBccId());
            prevBccRecord.update(BCC.NEXT_BCC_ID);

            bccManifestRecord.setBccId(nextBccRecord.getBccId());
            bccManifestRecord.setFromAccManifestId(accManifestRecord.getAccManifestId());
            bccManifestRecord.update(BCC_MANIFEST.BCC_ID, BCC_MANIFEST.FROM_ACC_MANIFEST_ID);
        }
    }

    public UpdateAccPropertiesRepositoryResponse updateAccProperties(UpdateAccPropertiesRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(
                        ULong.valueOf(request.getAccManifestId())
                ))
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

        // update acc record.
        accRecord.setObjectClassTerm(request.getObjectClassTerm());
        accRecord.setDen(accRecord.getObjectClassTerm() + ". Details");
        accRecord.setDefinition(request.getDefinition());
        accRecord.setDefinitionSource(request.getDefinitionSource());
        accRecord.setOagisComponentType(request.getComponentType().getValue());
        accRecord.setIsAbstract((byte) ((request.isAbstract()) ? 1 : 0));
        accRecord.setIsDeprecated((byte) ((request.isDeprecated()) ? 1 : 0));
        accRecord.setNamespaceId(ULong.valueOf(request.getNamespaceId()));
        accRecord.setLastUpdatedBy(userId);
        accRecord.setLastUpdateTimestamp(timestamp);
        accRecord.update(ACC.OBJECT_CLASS_TERM, ACC.DEN,
                ACC.DEFINITION, ACC.DEFINITION_SOURCE,
                ACC.OAGIS_COMPONENT_TYPE,
                ACC.IS_ABSTRACT, ACC.IS_DEPRECATED,
                ACC.NAMESPACE_ID,
                ACC.LAST_UPDATED_BY, ACC.LAST_UPDATE_TIMESTAMP);

        // creates new revision for updated record.
        RevisionRecord revisionRecord =
                revisionRepository.insertAccRevision(
                        accRecord, accManifestRecord.getRevisionId(),
                        RevisionAction.Modified,
                        userId, timestamp);

        accManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        accManifestRecord.update(ACC_MANIFEST.REVISION_ID);

        return new UpdateAccPropertiesRepositoryResponse(accManifestRecord.getAccManifestId().toBigInteger());
    }

    public UpdateAccBasedAccRepositoryResponse updateAccBasedAcc(UpdateAccBasedAccRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(
                        ULong.valueOf(request.getAccManifestId())
                ))
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

        // update acc record.
        if (request.getBasedAccManifestId() == null) {
            accRecord.setBasedAccId(null);
        } else {
            ULong basedAccId = dslContext.select(ACC_MANIFEST.ACC_ID)
                    .from(ACC_MANIFEST)
                    .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(request.getBasedAccManifestId())))
                    .fetchOneInto(ULong.class);

            accRecord.setBasedAccId(basedAccId);
        }
        accRecord.setLastUpdatedBy(userId);
        accRecord.setLastUpdateTimestamp(timestamp);
        accRecord.update(ACC.BASED_ACC_ID,
                ACC.LAST_UPDATED_BY, ACC.LAST_UPDATE_TIMESTAMP);

        // creates new revision for updated record.
        RevisionRecord revisionRecord =
                revisionRepository.insertAccRevision(
                        accRecord, accManifestRecord.getRevisionId(),
                        RevisionAction.Modified,
                        userId, timestamp);

        if (request.getBasedAccManifestId() == null) {
            accManifestRecord.setBasedAccManifestId(null);
        } else {
            accManifestRecord.setBasedAccManifestId(ULong.valueOf(request.getBasedAccManifestId()));
        }
        accManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        accManifestRecord.update(ACC_MANIFEST.BASED_ACC_MANIFEST_ID, ACC_MANIFEST.REVISION_ID);

        return new UpdateAccBasedAccRepositoryResponse(accManifestRecord.getAccManifestId().toBigInteger());
    }

    public UpdateAccStateRepositoryResponse updateAccState(UpdateAccStateRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(
                        ULong.valueOf(request.getAccManifestId())
                ))
                .fetchOne();

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId()))
                .fetchOne();

        CcState prevState = CcState.valueOf(accRecord.getState());
        if (CcState.Published.equals(prevState)) {
            throw new IllegalArgumentException("Only the core component not in 'Published' state can be modified.");
        }

        // @TODO: Add assertions by state transition model.
        // e.g. Draft -> Published would not allow.

        if (!accRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update acc state.
        accRecord.setState(request.getState().name());
        accRecord.setLastUpdatedBy(userId);
        accRecord.setLastUpdateTimestamp(timestamp);
        accRecord.update(ACC.STATE,
                ACC.LAST_UPDATED_BY, ACC.LAST_UPDATE_TIMESTAMP);

        // creates new revision for updated record.
        RevisionAction revisionAction = (CcState.Deleted == prevState && CcState.WIP == request.getState())
                ? RevisionAction.Restored : RevisionAction.Modified;
        RevisionRecord revisionRecord =
                revisionRepository.insertAccRevision(
                        accRecord, accManifestRecord.getRevisionId(),
                        revisionAction,
                        userId, timestamp);

        accManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        accManifestRecord.update(ACC_MANIFEST.REVISION_ID);

        return new UpdateAccStateRepositoryResponse(accManifestRecord.getAccManifestId().toBigInteger());
    }

    public DeleteAccRepositoryResponse deleteAcc(DeleteAccRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(
                        ULong.valueOf(request.getAccManifestId())
                ))
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
        accRecord.setState(CcState.Deleted.name());
        accRecord.setLastUpdatedBy(userId);
        accRecord.setLastUpdateTimestamp(timestamp);
        accRecord.update(ACC.STATE,
                ACC.LAST_UPDATED_BY, ACC.LAST_UPDATE_TIMESTAMP);

        // creates new revision for deleted record.
        RevisionRecord revisionRecord =
                revisionRepository.insertAccRevision(
                        accRecord, accManifestRecord.getRevisionId(),
                        RevisionAction.Deleted,
                        userId, timestamp);

        accManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        accManifestRecord.update(ACC_MANIFEST.REVISION_ID);

        return new DeleteAccRepositoryResponse(accManifestRecord.getAccManifestId().toBigInteger());
    }
}
