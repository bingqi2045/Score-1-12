package org.oagi.score.repo.component.acc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jooq.DSLContext;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.score.data.AppUser;
import org.oagi.score.data.BCCEntityType;
import org.oagi.score.data.OagisComponentType;
import org.oagi.score.data.RevisionAction;
import org.oagi.score.repo.entity.jooq.enums.SeqKeyType;
import org.oagi.score.repo.entity.jooq.tables.records.*;
import org.oagi.score.gateway.http.api.cc_management.data.CcId;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.helper.SrtGuid;
import org.oagi.score.repo.RevisionRepository;
import org.oagi.score.repo.component.seqkey.MoveTo;
import org.oagi.score.repo.component.seqkey.SeqKeyHandler;
import org.oagi.score.repo.domain.RevisionSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang3.StringUtils.compare;
import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.entity.jooq.Tables.*;
import static org.oagi.score.repo.entity.jooq.tables.Acc.ACC;
import static org.oagi.score.repo.entity.jooq.tables.AccManifest.ACC_MANIFEST;
import static org.oagi.score.repo.entity.jooq.tables.BccpManifest.BCCP_MANIFEST;

@Repository
public class AccWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RevisionRepository revisionRepository;

    @Autowired
    private RevisionSerializer serializer;

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
        acc.setOagisComponentType(request.getInitialComponentType().getValue());
        acc.setDefinition(request.getInitialDefinition());
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
        createNewAsccListForRevisedRecord(user, accManifestRecord, nextAccRecord, targetReleaseId, timestamp);
        createNewBccListForRevisedRecord(user, accManifestRecord, nextAccRecord, targetReleaseId, timestamp);
        linkSeqKeys(nextAccRecord);

        // creates new revision for revised record.
        RevisionRecord revisionRecord =
                revisionRepository.insertAccRevision(
                        nextAccRecord, accManifestRecord.getRevisionId(),
                        RevisionAction.Revised,
                        userId, timestamp);

        ULong responseAccManifestId;
        accManifestRecord.setAccId(nextAccRecord.getAccId());
        accManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        accManifestRecord.update(ACC_MANIFEST.ACC_ID, ACC_MANIFEST.REVISION_ID);

        responseAccManifestId = accManifestRecord.getAccManifestId();

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
            AppUser user,
            AccManifestRecord accManifestRecord,
            AccRecord nextAccRecord,
            ULong targetReleaseId,
            LocalDateTime timestamp) {
        ULong fromAccManifestId = accManifestRecord.getAccManifestId();
        for (AsccManifestRecord asccManifestRecord : dslContext.selectFrom(ASCC_MANIFEST)
                .where(and(
                        ASCC_MANIFEST.RELEASE_ID.eq(targetReleaseId),
                        ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(fromAccManifestId)
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
            nextAsccRecord.setCreatedBy(ULong.valueOf(user.getAppUserId()));
            nextAsccRecord.setLastUpdatedBy(ULong.valueOf(user.getAppUserId()));
            nextAsccRecord.setOwnerUserId(ULong.valueOf(user.getAppUserId()));
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

            ULong seqKeyId = dslContext.insertInto(SEQ_KEY)
                    .set(SEQ_KEY.FROM_ACC_ID, nextAccRecord.getAccId())
                    .set(SEQ_KEY.TYPE, SeqKeyType.ascc)
                    .set(SEQ_KEY.CC_ID, nextAsccRecord.getAsccId())
                    .returning(SEQ_KEY.SEQ_KEY_ID).fetchOne().getSeqKeyId();

            nextAsccRecord.setSeqKeyId(seqKeyId);
            nextAsccRecord.update(ASCC.SEQ_KEY_ID);

            asccManifestRecord.setAsccId(nextAsccRecord.getAsccId());
            asccManifestRecord.setFromAccManifestId(accManifestRecord.getAccManifestId());
            asccManifestRecord.update(ASCC_MANIFEST.ASCC_ID, ASCC_MANIFEST.FROM_ACC_MANIFEST_ID);
        }
    }

    private void createNewBccListForRevisedRecord(
            AppUser user,
            AccManifestRecord accManifestRecord,
            AccRecord nextAccRecord,
            ULong targetReleaseId,
            LocalDateTime timestamp) {
        ULong fromAccManifestId = accManifestRecord.getAccManifestId();
        for (BccManifestRecord bccManifestRecord : dslContext.selectFrom(BCC_MANIFEST)
                .where(and(
                        BCC_MANIFEST.RELEASE_ID.eq(targetReleaseId),
                        BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(fromAccManifestId)
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
            nextBccRecord.setCreatedBy(ULong.valueOf(user.getAppUserId()));
            nextBccRecord.setLastUpdatedBy(ULong.valueOf(user.getAppUserId()));
            nextBccRecord.setOwnerUserId(ULong.valueOf(user.getAppUserId()));
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

            ULong seqKeyId = dslContext.insertInto(SEQ_KEY)
                    .set(SEQ_KEY.FROM_ACC_ID, nextAccRecord.getAccId())
                    .set(SEQ_KEY.TYPE, SeqKeyType.bcc)
                    .set(SEQ_KEY.CC_ID, nextBccRecord.getBccId())
                    .returning(SEQ_KEY.SEQ_KEY_ID).fetchOne().getSeqKeyId();

            nextBccRecord.setSeqKeyId(seqKeyId);
            nextBccRecord.update(BCC.SEQ_KEY_ID);

            bccManifestRecord.setBccId(nextBccRecord.getBccId());
            bccManifestRecord.setFromAccManifestId(accManifestRecord.getAccManifestId());
            bccManifestRecord.update(BCC_MANIFEST.BCC_ID, BCC_MANIFEST.FROM_ACC_MANIFEST_ID);
        }
    }

    private void linkSeqKeys(AccRecord accRecord) {
        SeqKeyRecord prevHead = dslContext.selectFrom(SEQ_KEY)
                .where(and(SEQ_KEY.FROM_ACC_ID.eq(accRecord.getPrevAccId()),
                        SEQ_KEY.PREV_SEQ_KEY_ID.isNull())).fetchOne();
        List<ULong> orderedSeqIds = new ArrayList<>();
        if (prevHead != null) {
            while (prevHead.getNextSeqKeyId() != null) {
                orderedSeqIds.add(getNewSeqkeyIdByOldSeq(prevHead, accRecord));
                prevHead = dslContext.selectFrom(SEQ_KEY)
                        .where(SEQ_KEY.SEQ_KEY_ID.eq(prevHead.getNextSeqKeyId()))
                        .fetchOne();
            }
            orderedSeqIds.add(getNewSeqkeyIdByOldSeq(prevHead, accRecord));

            if (orderedSeqIds.size() < 2) {
                return;
            }
            for (ULong seqKey : orderedSeqIds) {
                ULong prev = orderedSeqIds.indexOf(seqKey) < 1 ? null : orderedSeqIds.get(orderedSeqIds.indexOf(seqKey) - 1);
                ULong next = orderedSeqIds.indexOf(seqKey) == orderedSeqIds.size() - 1 ? null : orderedSeqIds.get(orderedSeqIds.indexOf(seqKey) + 1);

                if (prev == null) {
                    dslContext.update(SEQ_KEY)
                            .set(SEQ_KEY.NEXT_SEQ_KEY_ID, next)
                            .where(SEQ_KEY.SEQ_KEY_ID.eq(seqKey)).execute();
                } else if (next == null) {
                    dslContext.update(SEQ_KEY)
                            .set(SEQ_KEY.PREV_SEQ_KEY_ID, prev)
                            .where(SEQ_KEY.SEQ_KEY_ID.eq(seqKey)).execute();
                } else {
                    dslContext.update(SEQ_KEY)
                            .set(SEQ_KEY.NEXT_SEQ_KEY_ID, next)
                            .set(SEQ_KEY.PREV_SEQ_KEY_ID, prev)
                            .where(SEQ_KEY.SEQ_KEY_ID.eq(seqKey)).execute();
                }
            }
        }
    }

    private ULong getNewSeqkeyIdByOldSeq(SeqKeyRecord seqKeyRecord, AccRecord accRecord) {
        if (seqKeyRecord.getType().equals(SeqKeyType.ascc)) {
            return dslContext.select(ASCC.as("next").SEQ_KEY_ID)
                    .from(ASCC.as("prev"))
                    .join(ASCC.as("next"))
                    .on(ASCC.as("prev").ASCC_ID.eq(ASCC.as("next").PREV_ASCC_ID))
                    .where(and(ASCC.as("prev").SEQ_KEY_ID.eq(seqKeyRecord.getSeqKeyId())),
                            ASCC.as("prev").FROM_ACC_ID.eq(accRecord.getPrevAccId()),
                            ASCC.as("next").FROM_ACC_ID.eq(accRecord.getAccId()))
                    .fetchOneInto(ULong.class);
        } else {
            return dslContext.select(BCC.as("next").SEQ_KEY_ID)
                    .from(BCC.as("prev"))
                    .join(BCC.as("next"))
                    .on(BCC.as("prev").BCC_ID.eq(BCC.as("next").PREV_BCC_ID))
                    .where(and(BCC.as("prev").SEQ_KEY_ID.eq(seqKeyRecord.getSeqKeyId())),
                            BCC.as("prev").FROM_ACC_ID.eq(accRecord.getPrevAccId()),
                            BCC.as("next").FROM_ACC_ID.eq(accRecord.getAccId()))
                    .fetchOneInto(ULong.class);
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

            for (AsccpManifestRecord asccpManifestRecord : dslContext.selectFrom(ASCCP_MANIFEST)
                    .where(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                    .fetch()) {

                AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                        .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId()))
                        .fetchOne();

                asccpRecord.setDen(asccpRecord.getPropertyTerm() + ". " + accRecord.getObjectClassTerm());
                asccpRecord.update(ASCCP.DEN);

                for (AsccManifestRecord asccManifestRecord : dslContext.selectFrom(ASCC_MANIFEST)
                        .where(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(asccpManifestRecord.getAsccpManifestId()))
                        .fetch()) {

                    AsccRecord asccRecord = dslContext.selectFrom(ASCC)
                            .where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId()))
                            .fetchOne();

                    String objectClassTerm = dslContext.select(ACC.OBJECT_CLASS_TERM)
                            .from(ACC).where(ACC.ACC_ID.eq(asccRecord.getFromAccId()))
                            .fetchOneInto(String.class);
                    asccRecord.setDen(objectClassTerm + ". " + asccpRecord.getDen());
                    asccRecord.update(ASCC.DEN);
                }
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
            AccManifestRecord basedAccManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                    .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(request.getBasedAccManifestId())))
                    .fetchOne();

            if (basedAccAlreadyContainAssociation(accManifestRecord, basedAccManifestRecord)) {
                throw new IllegalArgumentException("Based ACC that already contains an Association with the same property term.");
            }

            accRecord.setBasedAccId(basedAccManifestRecord.getAccId());
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

    private boolean basedAccAlreadyContainAssociation(AccManifestRecord accManifestRecord,
                                                      AccManifestRecord basedAccManifestRecord) {
        List<AsccManifestRecord> asccManifestRecords = dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                .fetch();

        List<BccManifestRecord> bccManifestRecords = dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                .fetch();
        
        List<ULong> asccpManifestIds = asccManifestRecords.stream()
                .map(AsccManifestRecord::getToAsccpManifestId).collect(Collectors.toList());

        List<ULong> bccpManifestIds = bccManifestRecords.stream()
                .map(BccManifestRecord::getToBccpManifestId).collect(Collectors.toList());


        while(basedAccManifestRecord != null) {
            if (dslContext.selectCount()
                    .from(ASCC_MANIFEST)
                    .where(and(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(basedAccManifestRecord.getAccManifestId()),
                            ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.in(asccpManifestIds)))
                    .fetchOptionalInto(Integer.class).orElse(0) > 0) {
                return true;
            }
            if (dslContext.selectCount()
                    .from(BCC_MANIFEST)
                    .where(and(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(basedAccManifestRecord.getAccManifestId()),
                            BCC_MANIFEST.TO_BCCP_MANIFEST_ID.in(bccpManifestIds)))
                    .fetchOptionalInto(Integer.class).orElse(0) > 0) {
                return true;
            }
            basedAccManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                    .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(basedAccManifestRecord.getBasedAccManifestId())).fetchOne();
        }
        return false;
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
        CcState nextState = request.getToState();

        if (prevState != request.getFromState()) {
            throw new IllegalArgumentException("Target core component is not in '" + request.getFromState() + "' state.");
        }

        if (!prevState.canMove(nextState)) {
            throw new IllegalArgumentException("The core component in '" + prevState + "' state cannot move to '" + nextState + "' state.");
        }

        // Change owner of CC when it restored.
        if (prevState == CcState.Deleted && nextState == CcState.WIP) {
            accRecord.setOwnerUserId(userId);
        } else if (prevState != CcState.Deleted && !accRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update acc state.
        accRecord.setState(nextState.name());
        accRecord.setLastUpdatedBy(userId);
        accRecord.setLastUpdateTimestamp(timestamp);
        accRecord.update(ACC.STATE,
                ACC.LAST_UPDATED_BY, ACC.LAST_UPDATE_TIMESTAMP, ACC.OWNER_USER_ID);

        // update associations' state.
        updateAsccListForStateUpdatedRecord(accManifestRecord, accRecord, nextState, userId, timestamp);
        updateBccListForStateUpdatedRecord(accManifestRecord, accRecord, nextState, userId, timestamp);

        // creates new revision for updated record.
        RevisionAction revisionAction = (CcState.Deleted == prevState && CcState.WIP == nextState)
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

            CcState prevState = CcState.valueOf(asccRecord.getState());

            // Change owner of CC when it restored.
            if (prevState == CcState.Deleted && nextState == CcState.WIP) {
                asccRecord.setOwnerUserId(userId);
            }

            asccRecord.setState(nextState.name());
            asccRecord.setLastUpdatedBy(userId);
            asccRecord.setLastUpdateTimestamp(timestamp);
            asccRecord.update(ASCC.FROM_ACC_ID, ASCC.TO_ASCCP_ID, ASCC.STATE,
                    ASCC.LAST_UPDATED_BY, ASCC.LAST_UPDATE_TIMESTAMP, ASCC.OWNER_USER_ID);
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

            CcState prevState = CcState.valueOf(bccRecord.getState());

            // Change owner of CC when it restored.
            if (prevState == CcState.Deleted && nextState == CcState.WIP) {
                bccRecord.setOwnerUserId(userId);
            }

            bccRecord.setState(nextState.name());
            bccRecord.setLastUpdatedBy(userId);
            bccRecord.setLastUpdateTimestamp(timestamp);
            bccRecord.update(BCC.FROM_ACC_ID, BCC.TO_BCCP_ID, BCC.STATE,
                    BCC.LAST_UPDATED_BY, BCC.LAST_UPDATE_TIMESTAMP, BCC.OWNER_USER_ID);
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

    public void moveSeq(UpdateSeqKeyRequest request) {
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

        moveSeq(userId, accRecord, request.getAccManifestId(),
                request.getItem(), request.getAfter());

        RevisionRecord revisionRecord =
                revisionRepository.insertAccRevision(
                        accRecord, accManifestRecord.getRevisionId(),
                        RevisionAction.Modified,
                        userId, timestamp);

        accManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        accManifestRecord.update(ACC_MANIFEST.REVISION_ID);
    }

    public void moveSeq(ULong userId, AccRecord accRecord, BigInteger accManifestId,
                        CcId item, CcId after) {
        SeqKeyHandler seqKeyHandler;

        switch (item.getType().toLowerCase()) {
            case "asccp":
                AsccRecord asccRecord = getAsccRecordForUpdateSeq(userId, accRecord, accManifestId, item);
                seqKeyHandler = new SeqKeyHandler(dslContext, asccRecord);
                break;

            case "bccp":
                BccRecord bccRecord = getBccRecordForUpdateSeq(userId, accRecord, accManifestId, item);
                seqKeyHandler = new SeqKeyHandler(dslContext, bccRecord);
                break;

            default:
                throw new IllegalArgumentException();
        }

        if (after == null) {
            seqKeyHandler.moveTo(MoveTo.FIRST);
        } else {
            SeqKeyRecord seqKeyRecord;
            switch (after.getType().toLowerCase()) {
                case "asccp":
                    AsccRecord asccRecord = getAsccRecordForUpdateSeq(userId, accRecord, accManifestId, after);
                    seqKeyRecord = dslContext.selectFrom(SEQ_KEY)
                            .where(SEQ_KEY.SEQ_KEY_ID.eq(asccRecord.getSeqKeyId()))
                            .fetchOne();
                    break;

                case "bccp":
                    BccRecord bccRecord = getBccRecordForUpdateSeq(userId, accRecord, accManifestId, after);
                    seqKeyRecord = dslContext.selectFrom(SEQ_KEY)
                            .where(SEQ_KEY.SEQ_KEY_ID.eq(bccRecord.getSeqKeyId()))
                            .fetchOne();
                    break;

                default:
                    throw new IllegalArgumentException();
            }
            seqKeyHandler.moveAfter(seqKeyRecord);
        }
    }

    private AsccRecord getAsccRecordForUpdateSeq(ULong userId, AccRecord accRecord, BigInteger accManidestId, CcId ccId) {
        AsccRecord asccRecord = dslContext
                .select(ASCC.ASCC_ID, ASCC.SEQ_KEY_ID, ASCC.FROM_ACC_ID, ASCC.OWNER_USER_ID)
                .from(ASCC)
                .join(ASCC_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
                .where(
                        and(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ULong.valueOf(ccId.getManifestId())),
                                ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ULong.valueOf(accManidestId)))
                )
                .fetchOneInto(AsccRecord.class);

        if (!asccRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }
        if (!asccRecord.getFromAccId().equals(accRecord.getAccId())) {
            throw new IllegalArgumentException("It only allows to modify the core component for the corresponding component.");
        }

        return asccRecord;
    }

    private BccRecord getBccRecordForUpdateSeq(ULong userId, AccRecord accRecord, BigInteger accManidestId, CcId ccId) {
        BccRecord bccRecord = dslContext
                .select(BCC.BCC_ID, BCC.SEQ_KEY_ID, BCC.FROM_ACC_ID, BCC.OWNER_USER_ID)
                .from(BCC)
                .join(BCC_MANIFEST)
                .on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                .where(
                        and(BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(ULong.valueOf(ccId.getManifestId()))),
                        BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ULong.valueOf(accManidestId)))
                .fetchOneInto(BccRecord.class);

        if (!bccRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }
        if (!bccRecord.getFromAccId().equals(accRecord.getAccId())) {
            throw new IllegalArgumentException("It only allows to modify the core component for the corresponding component.");
        }

        return bccRecord;
    }

    public DiscardRevisionAccRepositoryResponse discardRevisionAcc(DiscardRevisionAccRepositoryRequest request) {
        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(request.getAccManifestId()))).fetchOne();

        if (accManifestRecord == null) {
            throw new IllegalArgumentException("Not found a target ACC");
        }

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();

        if (accRecord.getPrevAccId() == null) {
            throw new IllegalArgumentException("Not found previous revision");
        }

        RevisionRecord cursorRevision = dslContext.selectFrom(REVISION)
                .where(REVISION.REVISION_ID.eq(accManifestRecord.getRevisionId())).fetchOne();

        UInteger revisionNum = cursorRevision.getRevisionNum();

        if (cursorRevision.getPrevRevisionId() == null) {
            throw new IllegalArgumentException("There is no change to be reset.");
        }

        List<ULong> deleteRevisionTargets = new ArrayList<>();

        while(cursorRevision.getPrevRevisionId() != null) {
            if(cursorRevision.getRevisionNum().compareTo(revisionNum) < 0) {
                break;
            }
            deleteRevisionTargets.add(cursorRevision.getRevisionId());
            cursorRevision = dslContext.selectFrom(REVISION)
                    .where(REVISION.REVISION_ID.eq(cursorRevision.getPrevRevisionId())).fetchOne();
        }

        // update ACC MANIFEST's acc_id and revision_id
        accManifestRecord.setAccId(accRecord.getPrevAccId());
        accManifestRecord.setRevisionId(cursorRevision.getRevisionId());
        accManifestRecord.update(ACC_MANIFEST.ACC_ID, ACC_MANIFEST.REVISION_ID);

        // unlink revision
        cursorRevision.setNextRevisionId(null);
        cursorRevision.update(REVISION.NEXT_REVISION_ID);
        dslContext.update(REVISION)
                .setNull(REVISION.PREV_REVISION_ID)
                .setNull(REVISION.NEXT_REVISION_ID)
                .where(REVISION.REVISION_ID.in(deleteRevisionTargets))
                .execute();
        dslContext.deleteFrom(REVISION).where(REVISION.REVISION_ID.in(deleteRevisionTargets)).execute();

        discardRevisionAssociations(accManifestRecord, accRecord);

        AccRecord prevAccRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accRecord.getPrevAccId())).fetchOne();

        // unlink prev ACC
        prevAccRecord.setNextAccId(null);
        prevAccRecord.update(ACC.NEXT_ACC_ID);

        // delete current ACC
        accRecord.delete();

        return new DiscardRevisionAccRepositoryResponse(request.getAccManifestId());
    }

    private void discardRevisionAssociations(AccManifestRecord accManifestRecord, AccRecord accRecord) {
        List<AsccManifestRecord> asccManifestRecords = dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId())).fetch();

        List<BccManifestRecord> bccManifestRecords = dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId())).fetch();

        for (AsccManifestRecord asccManifestRecord : asccManifestRecords) {
            AsccRecord asccRecord = dslContext.selectFrom(ASCC)
                    .where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId())).fetchOne();
            
            if (asccRecord.getPrevAsccId() == null) {
                //delete ascc and ascc manifest which added this revision
                asccManifestRecord.delete();
                asccRecord.delete();
            } else {
                //delete ascc and update ascc manifest
                AsccRecord prevAsccRecord = dslContext.selectFrom(ASCC)
                        .where(ASCC.ASCC_ID.eq(asccRecord.getPrevAsccId())).fetchOne();
                prevAsccRecord.setNextAsccId(null);
                prevAsccRecord.update(ASCC.NEXT_ASCC_ID);
                asccManifestRecord.setAsccId(prevAsccRecord.getAsccId());
                asccManifestRecord.update(ASCC_MANIFEST.ASCC_ID);
                asccRecord.delete();
            }
        }

        for (BccManifestRecord bccManifestRecord : bccManifestRecords) {
            BccRecord bccRecord = dslContext.selectFrom(BCC)
                    .where(BCC.BCC_ID.eq(bccManifestRecord.getBccId())).fetchOne();

            if (bccRecord.getPrevBccId() == null) {
                //delete bcc and bcc manifest which added this revision
                bccManifestRecord.delete();
                bccRecord.delete();
            } else {
                //delete bcc and update bcc manifest
                BccRecord prevBccRecord = dslContext.selectFrom(BCC)
                        .where(BCC.BCC_ID.eq(bccRecord.getPrevBccId())).fetchOne();
                prevBccRecord.setNextBccId(null);
                prevBccRecord.update(BCC.NEXT_BCC_ID);
                bccManifestRecord.setBccId(prevBccRecord.getBccId());
                bccManifestRecord.update(BCC_MANIFEST.BCC_ID);
                bccRecord.delete();
            }
        }

        // update ACCs which using with based current ACC
        dslContext.update(ACC)
                .set(ACC.BASED_ACC_ID, accRecord.getPrevAccId())
                .where(ACC.BASED_ACC_ID.eq(accRecord.getAccId()))
                .execute();

        // update ASCCPs which using with role of current ACC
        dslContext.update(ASCCP)
                .set(ASCCP.ROLE_OF_ACC_ID, accRecord.getPrevAccId())
                .where(ASCCP.ROLE_OF_ACC_ID.eq(accRecord.getAccId()))
                .execute();

        // delete SEQ_KEY for current ACC
        dslContext.update(SEQ_KEY)
                .setNull(SEQ_KEY.PREV_SEQ_KEY_ID)
                .setNull(SEQ_KEY.NEXT_SEQ_KEY_ID)
                .where(SEQ_KEY.FROM_ACC_ID.eq(accRecord.getAccId())).execute();
        dslContext.deleteFrom(SEQ_KEY).where(SEQ_KEY.FROM_ACC_ID.eq(accRecord.getAccId())).execute();
    }

    public DiscardRevisionAccRepositoryResponse resetRevisionAcc(DiscardRevisionAccRepositoryRequest request) {
        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(request.getAccManifestId()))).fetchOne();

        if (accManifestRecord == null) {
            throw new IllegalArgumentException("Not found a target ACC");
        }

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();

        RevisionRecord cursorRevision = dslContext.selectFrom(REVISION)
                .where(REVISION.REVISION_ID.eq(accManifestRecord.getRevisionId())).fetchOne();

        UInteger revisionNum = cursorRevision.getRevisionNum();

        if (cursorRevision.getPrevRevisionId() == null) {
            throw new IllegalArgumentException("There is no change to be reset.");
        }

        List<ULong> deleteRevisionTargets = new ArrayList<>();

        while(cursorRevision.getPrevRevisionId() != null) {
            if (!cursorRevision.getRevisionNum().equals(revisionNum)) {
                throw new IllegalArgumentException("Can not found reset point");
            }
            if(cursorRevision.getRevisionTrackingNum().equals(UInteger.valueOf(1))) {
                break;
            }
            deleteRevisionTargets.add(cursorRevision.getRevisionId());
            cursorRevision = dslContext.selectFrom(REVISION)
                    .where(REVISION.REVISION_ID.eq(cursorRevision.getPrevRevisionId())).fetchOne();
        }

        JsonObject snapshot = serializer.deserialize(cursorRevision.getSnapshot().toString());

        ULong basedAccId = serializer.getSnapshotId(snapshot.get("basedAccId"));

        if (basedAccId != null) {
            AccManifestRecord basedAccManifestRecord = dslContext.selectFrom(ACC_MANIFEST).where(and(
                    ACC_MANIFEST.ACC_ID.eq(basedAccId),
                    ACC_MANIFEST.RELEASE_ID.eq(accManifestRecord.getReleaseId())
            )).fetchOne();

            if (basedAccManifestRecord == null) {
                throw new IllegalArgumentException("Not found based ACC.");
            }

            accManifestRecord.setBasedAccManifestId(basedAccManifestRecord.getAccManifestId());
            accRecord.setBasedAccId(basedAccManifestRecord.getAccId());
        } else {
            accManifestRecord.setBasedAccManifestId(null);
            accRecord.setBasedAccId(null);
        }
        accManifestRecord.setRevisionId(cursorRevision.getRevisionId());
        accManifestRecord.update(ACC_MANIFEST.BASED_ACC_MANIFEST_ID, ACC_MANIFEST.REVISION_ID);

        accRecord.setObjectClassTerm(serializer.getSnapshotString(snapshot.get("objectClassTerm")));
        accRecord.setDen(accRecord.getObjectClassTerm() + ". Details");
        accRecord.setDefinition(serializer.getSnapshotString(snapshot.get("definition")));
        accRecord.setDefinitionSource(serializer.getSnapshotString(snapshot.get("definitionSource")));
        accRecord.setOagisComponentType(OagisComponentType.valueOf(
                serializer.getSnapshotString(snapshot.get("componentType"))).getValue());
        accRecord.setNamespaceId(serializer.getSnapshotId(snapshot.get("namespaceId")));
        accRecord.setIsDeprecated(serializer.getSnapshotByte(snapshot.get("deprecated")));
        accRecord.setIsAbstract(serializer.getSnapshotByte(snapshot.get("abstract")));
        accRecord.update();

        resetAssociations(snapshot.get("associations"), accManifestRecord);

        cursorRevision.setNextRevisionId(null);
        cursorRevision.update(REVISION.NEXT_REVISION_ID);
        dslContext.update(REVISION)
                .setNull(REVISION.PREV_REVISION_ID)
                .setNull(REVISION.NEXT_REVISION_ID)
                .where(REVISION.REVISION_ID.in(deleteRevisionTargets))
                .execute();
        dslContext.deleteFrom(REVISION).where(REVISION.REVISION_ID.in(deleteRevisionTargets)).execute();

        return new DiscardRevisionAccRepositoryResponse(request.getAccManifestId());
    }

    private void resetAssociations(JsonElement associationElement, AccManifestRecord accManifestRecord) {
        JsonArray associations = associationElement.getAsJsonArray();
        int associationCount = associations.size();

        List<AsccManifestRecord> asccManifestRecords = dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId())).fetch();

        List<BccManifestRecord> bccManifestRecords = dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId())).fetch();

        List<JsonObject> associationObjects = IntStream.range(0, associationCount)
                .mapToObj(i -> associations.get(i).getAsJsonObject())
                .collect(Collectors.toList());
        for (AsccManifestRecord asccManifestRecord : asccManifestRecords) {
            AsccRecord asccRecord = dslContext.selectFrom(ASCC)
                    .where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId())).fetchOne();
            JsonObject asccObject = associationObjects.stream().filter(o ->
                serializer.getSnapshotString(o.get("component")).equals("ascc")
                        && serializer.getSnapshotString(o.get("guid")).equals(asccRecord.getGuid())
            ).findFirst().orElse(null);

            if (asccObject == null) {
                asccRecord.setState(CcState.Deleted.name());
            } else {
                asccRecord.setCardinalityMin(asccObject.get("cardinalityMin").getAsInt());
                asccRecord.setCardinalityMax(asccObject.get("cardinalityMax").getAsInt());
                asccRecord.setIsDeprecated(serializer.getSnapshotByte(asccObject.get("deprecated")));
                asccRecord.setDefinition(serializer.getSnapshotString(asccObject.get("definition")));
                asccRecord.setDefinitionSource(serializer.getSnapshotString(asccObject.get("definitionSource")));
            }
            asccRecord.update();
        }

        for (BccManifestRecord bccManifestRecord : bccManifestRecords) {
            BccRecord bccRecord = dslContext.selectFrom(BCC)
                    .where(BCC.BCC_ID.eq(bccManifestRecord.getBccId())).fetchOne();
            JsonObject bccObject = associationObjects.stream().filter(o ->
                    serializer.getSnapshotString(o.get("component")).equals("bcc")
                            && serializer.getSnapshotString(o.get("guid")).equals(bccRecord.getGuid())
            ).findFirst().orElse(null);

            if (bccObject == null) {
                bccRecord.setState(CcState.Deleted.name());
            } else {
                bccRecord.setCardinalityMin(bccObject.get("cardinalityMin").getAsInt());
                bccRecord.setCardinalityMax(bccObject.get("cardinalityMax").getAsInt());
                bccRecord.setIsDeprecated(serializer.getSnapshotByte(bccObject.get("deprecated")));
                bccRecord.setDefinition(serializer.getSnapshotString(bccObject.get("definition")));
                bccRecord.setDefinitionSource(serializer.getSnapshotString(bccObject.get("definitionSource")));
                bccRecord.setEntityType(BCCEntityType.valueOf(
                        serializer.getSnapshotString(bccObject.get("entityType"))).getValue());
                bccRecord.setDefaultValue(serializer.getSnapshotString(bccObject.get("defaultValue")));
                bccRecord.setFixedValue(serializer.getSnapshotString(bccObject.get("fixedValue")));
            }
            bccRecord.update();
        }
    }
}