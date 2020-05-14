package org.oagi.srt.repo.component.acc;

import org.jooq.DSLContext;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
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

import static org.apache.commons.lang3.StringUtils.compare;
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

        if (user.isDeveloper()) {
            if (!CcState.Published.equals(CcState.valueOf(prevAccRecord.getState()))) {
                throw new IllegalArgumentException("Only the core component in 'Published' state can be revised.");
            }
        } else {
            if (!CcState.Production.equals(CcState.valueOf(prevAccRecord.getState()))) {
                throw new IllegalArgumentException("Only the core component in 'Production' state can be revised.");
            }
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
        boolean denNeedsToUpdate = false;
        UpdateSetFirstStep<AccRecord> firstStep = dslContext.update(ACC);
        UpdateSetMoreStep<AccRecord> moreStep = null;
        if (compare(accRecord.getObjectClassTerm(), request.getObjectClassTerm()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ACC.OBJECT_CLASS_TERM, request.getObjectClassTerm())
                    .set(ACC.DEN, request.getObjectClassTerm() + ". Details");
            denNeedsToUpdate = true;
        }
        if (compare(accRecord.getDefinition(), request.getDefinition()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ACC.DEFINITION, request.getDefinition());
        }
        if (compare(accRecord.getDefinitionSource(), request.getDefinitionSource()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ACC.DEFINITION_SOURCE, request.getDefinitionSource());
        }
        if (request.getComponentType() != null) {
            if (accRecord.getOagisComponentType() != request.getComponentType().getValue()) {
                moreStep = ((moreStep != null) ? moreStep : firstStep)
                        .set(ACC.OAGIS_COMPONENT_TYPE, request.getComponentType().getValue());
            }
        }
        if ((accRecord.getIsAbstract() == 1 ? true : false) != request.isAbstract()) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ACC.IS_ABSTRACT, (byte) ((request.isAbstract()) ? 1 : 0));
        }
        if ((accRecord.getIsDeprecated() == 1 ? true : false) != request.isDeprecated()) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ACC.IS_DEPRECATED, (byte) ((request.isDeprecated()) ? 1 : 0));
        }
        if (request.getNamespaceId() == null || request.getNamespaceId().longValue() <= 0L) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .setNull(ACC.NAMESPACE_ID);
        } else {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ACC.NAMESPACE_ID, ULong.valueOf(request.getNamespaceId()));
        }

        if (moreStep != null) {
            moreStep.set(ACC.LAST_UPDATED_BY, userId)
                    .set(ACC.LAST_UPDATE_TIMESTAMP, timestamp)
                    .where(ACC.ACC_ID.eq(accRecord.getAccId()))
                    .execute();

            accRecord = dslContext.selectFrom(ACC)
                    .where(ACC.ACC_ID.eq(accManifestRecord.getAccId()))
                    .fetchOne();
        }

        if (denNeedsToUpdate) {
            for (AsccManifestRecord asccManifestRecord : dslContext.selectFrom(ASCC_MANIFEST)
                    .where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                    .fetch()) {

                AsccRecord asccRecord = dslContext.selectFrom(ASCC)
                        .where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId()))
                        .fetchOne();

                String asccpDen = dslContext.select(ASCCP.DEN)
                        .from(ASCCP)
                        .join(ASCCP_MANIFEST).on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                        .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(asccManifestRecord.getToAsccpManifestId()))
                        .fetchOneInto(String.class);

                asccRecord.setDen(accRecord.getObjectClassTerm() + ". " + asccpDen);
                asccRecord.update(ASCC.DEN);
            }

            for (BccManifestRecord bccManifestRecord : dslContext.selectFrom(BCC_MANIFEST)
                    .where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                    .fetch()) {

                BccRecord bccRecord = dslContext.selectFrom(BCC)
                        .where(BCC.BCC_ID.eq(bccManifestRecord.getBccId()))
                        .fetchOne();

                String bccpDen = dslContext.select(BCCP.DEN)
                        .from(BCCP)
                        .join(BCCP_MANIFEST).on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                        .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(bccManifestRecord.getToBccpManifestId()))
                        .fetchOneInto(String.class);

                bccRecord.setDen(accRecord.getObjectClassTerm() + ". " + bccpDen);
                bccRecord.update(BCC.DEN);
            }
        }

        if (moreStep != null) {
            // creates new revision for updated record.
            RevisionRecord revisionRecord =
                    revisionRepository.insertAccRevision(
                            accRecord, accManifestRecord.getRevisionId(),
                            RevisionAction.Modified,
                            userId, timestamp);

            accManifestRecord.setRevisionId(revisionRecord.getRevisionId());
            accManifestRecord.update(ACC_MANIFEST.REVISION_ID);
        }

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
        CcState nextState = request.getState();

        if (!prevState.canMove(nextState)) {
            throw new IllegalArgumentException("The core component in '" + prevState + "' state cannot move to '" + nextState + "' state.");
        }

        if (!accRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update acc state.
        accRecord.setState(nextState.name());
        accRecord.setLastUpdatedBy(userId);
        accRecord.setLastUpdateTimestamp(timestamp);
        accRecord.update(ACC.STATE,
                ACC.LAST_UPDATED_BY, ACC.LAST_UPDATE_TIMESTAMP);

        // update associations' state.
        updateAsccListForStateUpdatedRecord(accManifestRecord, accRecord, CcState.Deleted, userId, timestamp);
        updateBccListForStateUpdatedRecord(accManifestRecord, accRecord, CcState.Deleted, userId, timestamp);

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

    private void updateAsccListForStateUpdatedRecord(
            AccManifestRecord accManifestRecord,
            AccRecord nextAccRecord,
            CcState nextState,
            ULong userId,
            LocalDateTime timestamp) {
        for (AsccManifestRecord asccManifestRecord : dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                .fetch()) {

            AsccRecord asccRecord = dslContext.selectFrom(ASCC)
                    .where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId()))
                    .fetchOne();

            asccRecord.setFromAccId(nextAccRecord.getAccId());
            asccRecord.setToAsccpId(
                    dslContext.select(ASCCP_MANIFEST.ASCCP_ID)
                            .from(ASCCP_MANIFEST)
                            .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(asccManifestRecord.getToAsccpManifestId()))
                            .fetchOneInto(ULong.class)
            );
            asccRecord.setState(nextState.name());
            asccRecord.setLastUpdatedBy(userId);
            asccRecord.setLastUpdateTimestamp(timestamp);
            asccRecord.update(ASCC.FROM_ACC_ID, ASCC.TO_ASCCP_ID, ASCC.STATE,
                    ASCC.LAST_UPDATED_BY, ASCC.LAST_UPDATE_TIMESTAMP);
        }
    }

    private void updateBccListForStateUpdatedRecord(
            AccManifestRecord accManifestRecord,
            AccRecord nextAccRecord,
            CcState nextState,
            ULong userId,
            LocalDateTime timestamp) {
        for (BccManifestRecord bccManifestRecord : dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                .fetch()) {

            BccRecord bccRecord = dslContext.selectFrom(BCC)
                    .where(BCC.BCC_ID.eq(bccManifestRecord.getBccId()))
                    .fetchOne();

            bccRecord.setFromAccId(nextAccRecord.getAccId());
            bccRecord.setToBccpId(
                    dslContext.select(BCCP_MANIFEST.BCCP_ID)
                            .from(BCCP_MANIFEST)
                            .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(bccManifestRecord.getToBccpManifestId()))
                            .fetchOneInto(ULong.class)
            );
            bccRecord.setState(nextState.name());
            bccRecord.setLastUpdatedBy(userId);
            bccRecord.setLastUpdateTimestamp(timestamp);
            bccRecord.update(BCC.FROM_ACC_ID, BCC.TO_BCCP_ID, BCC.STATE,
                    BCC.LAST_UPDATED_BY, BCC.LAST_UPDATE_TIMESTAMP);
        }
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

        // delete associations.
        updateAsccListForStateUpdatedRecord(accManifestRecord, accRecord, CcState.Deleted, userId, timestamp);
        updateBccListForStateUpdatedRecord(accManifestRecord, accRecord, CcState.Deleted, userId, timestamp);

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

    public UpdateAccOwnerRepositoryResponse updateAccOwner(UpdateAccOwnerRepositoryRequest request) {
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

        accRecord.setOwnerUserId(ULong.valueOf(request.getOwnerId()));
        accRecord.setLastUpdatedBy(userId);
        accRecord.setLastUpdateTimestamp(timestamp);
        accRecord.update(ACC.OWNER_USER_ID, ACC.LAST_UPDATED_BY, ACC.LAST_UPDATE_TIMESTAMP);

        for (AsccManifestRecord asccManifestRecord : dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                .fetch()) {

            AsccRecord asccRecord = dslContext.selectFrom(ASCC)
                    .where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId()))
                    .fetchOne();

            asccRecord.setOwnerUserId(ULong.valueOf(request.getOwnerId()));
            asccRecord.update(ASCC.OWNER_USER_ID);
        }

        for (BccManifestRecord bccManifestRecord : dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                .fetch()) {

            BccRecord bccRecord = dslContext.selectFrom(BCC)
                    .where(BCC.BCC_ID.eq(bccManifestRecord.getBccId()))
                    .fetchOne();

            bccRecord.setOwnerUserId(ULong.valueOf(request.getOwnerId()));
            bccRecord.update(BCC.OWNER_USER_ID);
        }

        RevisionRecord revisionRecord =
                revisionRepository.insertAccRevision(
                        accRecord, accManifestRecord.getRevisionId(),
                        RevisionAction.Modified,
                        userId, timestamp);

        accManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        accManifestRecord.update(ACC_MANIFEST.REVISION_ID);

        return new UpdateAccOwnerRepositoryResponse(accManifestRecord.getAccManifestId().toBigInteger());
    }
}