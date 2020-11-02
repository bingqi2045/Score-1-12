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
import org.oagi.score.data.LogAction;
import org.oagi.score.data.OagisComponentType;
import org.oagi.score.gateway.http.api.cc_management.data.CcId;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.helper.ScoreGuid;
import org.oagi.score.repo.LogRepository;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.corecomponent.seqkey.model.GetSeqKeyRequest;
import org.oagi.score.repo.api.corecomponent.seqkey.model.SeqKey;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.repo.domain.LogSerializer;
import org.oagi.score.service.corecomponent.seqkey.MoveTo;
import org.oagi.score.service.corecomponent.seqkey.SeqKeyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
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
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.Acc.ACC;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.AccManifest.ACC_MANIFEST;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.BccpManifest.BCCP_MANIFEST;

@Repository
public class AccWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private LogSerializer serializer;

    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    private String objectClassTerm(ULong accId) {
        return dslContext.select(ACC.OBJECT_CLASS_TERM).from(ACC)
                .where(ACC.ACC_ID.eq(accId))
                .fetchOneInto(String.class);
    }

    public CreateAccRepositoryResponse createAcc(CreateAccRepositoryRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(request.getUser()));
        LocalDateTime timestamp = request.getLocalDateTime();
        AccManifestRecord basedAccManifest = null;

        if (request.getBasedAccManifestId() != null) {
            basedAccManifest = dslContext.selectFrom(ACC_MANIFEST)
                    .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(request.getBasedAccManifestId()))).fetchOne();
        }

        AccRecord acc = new AccRecord();
        acc.setGuid(ScoreGuid.randomGuid());
        acc.setObjectClassTerm(request.getInitialObjectClassTerm());
        acc.setDen(acc.getObjectClassTerm() + ". Details");
        acc.setOagisComponentType(request.getInitialComponentType().getValue());
        acc.setType(request.getInitialType().name());
        acc.setDefinition(request.getInitialDefinition());
        acc.setState(CcState.WIP.name());
        acc.setIsAbstract((byte) 0);
        acc.setIsDeprecated((byte) 0);
        if (basedAccManifest != null) {
            acc.setBasedAccId(basedAccManifest.getAccId());
        }
        if (request.getNamespaceId() != null) {
            acc.setNamespaceId(ULong.valueOf(request.getNamespaceId()));
        }
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
        if (basedAccManifest != null) {
            accManifest.setBasedAccManifestId(basedAccManifest.getAccManifestId());
        }

        LogRecord logRecord =
                logRepository.insertAccLog(
                        accManifest,
                        acc,
                        LogAction.Added,
                        userId, timestamp);
        accManifest.setLogId(logRecord.getLogId());

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
        linkSeqKeys(accManifestRecord);

        // creates new revision for revised record.
        LogRecord logRecord =
                logRepository.insertAccLog(accManifestRecord,
                        nextAccRecord, accManifestRecord.getLogId(),
                        LogAction.Revised,
                        userId, timestamp);

        ULong responseAccManifestId;
        accManifestRecord.setAccId(nextAccRecord.getAccId());
        accManifestRecord.setLogId(logRecord.getLogId());
        accManifestRecord.update(ACC_MANIFEST.ACC_ID, ACC_MANIFEST.LOG_ID);

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
                    .set(SEQ_KEY.FROM_ACC_MANIFEST_ID, accManifestRecord.getAccManifestId())
                    .set(SEQ_KEY.ASCC_MANIFEST_ID, asccManifestRecord.getAsccManifestId())
                    .returning(SEQ_KEY.SEQ_KEY_ID).fetchOne().getSeqKeyId();

            asccManifestRecord.setAsccId(nextAsccRecord.getAsccId());
            asccManifestRecord.setFromAccManifestId(accManifestRecord.getAccManifestId());
            asccManifestRecord.setSeqKeyId(seqKeyId);
            asccManifestRecord.update(ASCC_MANIFEST.ASCC_ID, ASCC_MANIFEST.FROM_ACC_MANIFEST_ID, ASCC_MANIFEST.SEQ_KEY_ID);
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
                    .set(SEQ_KEY.FROM_ACC_MANIFEST_ID, accManifestRecord.getAccManifestId())
                    .set(SEQ_KEY.BCC_MANIFEST_ID, bccManifestRecord.getBccManifestId())
                    .returning(SEQ_KEY.SEQ_KEY_ID).fetchOne().getSeqKeyId();

            bccManifestRecord.setBccId(nextBccRecord.getBccId());
            bccManifestRecord.setFromAccManifestId(accManifestRecord.getAccManifestId());
            bccManifestRecord.setSeqKeyId(seqKeyId);
            bccManifestRecord.update(BCC_MANIFEST.BCC_ID, BCC_MANIFEST.FROM_ACC_MANIFEST_ID);
        }
    }

    private void linkSeqKeys(AccManifestRecord accManifestRecordRecord) {
        SeqKeyRecord prevHead = dslContext.selectFrom(SEQ_KEY)
                .where(and(SEQ_KEY.FROM_ACC_MANIFEST_ID.eq(accManifestRecordRecord.getPrevAccManifestId()),
                        SEQ_KEY.PREV_SEQ_KEY_ID.isNull())).fetchOne();
        List<ULong> orderedSeqIds = new ArrayList<>();
        if (prevHead != null) {
            while (prevHead.getNextSeqKeyId() != null) {
                orderedSeqIds.add(getNewSeqkeyIdByOldSeq(prevHead, accManifestRecordRecord));
                prevHead = dslContext.selectFrom(SEQ_KEY)
                        .where(SEQ_KEY.SEQ_KEY_ID.eq(prevHead.getNextSeqKeyId()))
                        .fetchOne();
            }
            orderedSeqIds.add(getNewSeqkeyIdByOldSeq(prevHead, accManifestRecordRecord));

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

    private ULong getNewSeqkeyIdByOldSeq(SeqKeyRecord seqKeyRecord, AccManifestRecord accManifestRecord) {
        if (seqKeyRecord.getAsccManifestId() != null) {
            return dslContext.select(ASCC_MANIFEST.as("next").SEQ_KEY_ID)
                    .from(ASCC_MANIFEST.as("prev"))
                    .join(ASCC_MANIFEST.as("next"))
                    .on(ASCC_MANIFEST.as("prev").ASCC_MANIFEST_ID.eq(ASCC_MANIFEST.as("next").PREV_ASCC_MANIFEST_ID))
                    .where(and(ASCC_MANIFEST.as("prev").SEQ_KEY_ID.eq(seqKeyRecord.getSeqKeyId())),
                            ASCC_MANIFEST.as("prev").FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getPrevAccManifestId()),
                            ASCC_MANIFEST.as("next").FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                    .fetchOneInto(ULong.class);
        } else {
            return dslContext.select(BCC_MANIFEST.as("next").SEQ_KEY_ID)
                    .from(BCC_MANIFEST.as("prev"))
                    .join(BCC_MANIFEST.as("next"))
                    .on(BCC_MANIFEST.as("prev").BCC_MANIFEST_ID.eq(BCC_MANIFEST.as("next").PREV_BCC_MANIFEST_ID))
                    .where(and(BCC_MANIFEST.as("prev").SEQ_KEY_ID.eq(seqKeyRecord.getSeqKeyId())),
                            BCC_MANIFEST.as("prev").FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getPrevAccManifestId()),
                            BCC_MANIFEST.as("next").FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
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
        if ((accRecord.getIsAbstract() == 1) != request.isAbstract()) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ACC.IS_ABSTRACT, (byte) ((request.isAbstract()) ? 1 : 0));
        }
        if ((accRecord.getIsDeprecated() == 1) != request.isDeprecated()) {
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
            LogRecord logRecord =
                    logRepository.insertAccLog(accManifestRecord,
                            accRecord, accManifestRecord.getLogId(),
                            LogAction.Modified,
                            userId, timestamp);

            accManifestRecord.setLogId(logRecord.getLogId());
            accManifestRecord.update(ACC_MANIFEST.LOG_ID);
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
        LogRecord logRecord =
                logRepository.insertAccLog(accManifestRecord,
                        accRecord, accManifestRecord.getLogId(),
                        LogAction.Modified,
                        userId, timestamp);

        if (request.getBasedAccManifestId() == null) {
            accManifestRecord.setBasedAccManifestId(null);
        } else {
            accManifestRecord.setBasedAccManifestId(ULong.valueOf(request.getBasedAccManifestId()));
        }
        accManifestRecord.setLogId(logRecord.getLogId());
        accManifestRecord.update(ACC_MANIFEST.BASED_ACC_MANIFEST_ID, ACC_MANIFEST.LOG_ID);

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


        while (basedAccManifestRecord != null) {
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
        } else if (prevState != CcState.Deleted && !accRecord.getOwnerUserId().equals(userId)
                && !prevState.canForceMove(request.getToState())) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        } else if (accRecord.getNamespaceId() == null) {
            throw new IllegalArgumentException("'" + accRecord.getDen() + "' namespace required.");
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
        LogAction logAction = (CcState.Deleted == prevState && CcState.WIP == nextState)
                ? LogAction.Restored : LogAction.Modified;
        LogRecord logRecord =
                logRepository.insertAccLog(accManifestRecord,
                        accRecord, accManifestRecord.getLogId(),
                        logAction,
                        userId, timestamp);

        accManifestRecord.setLogId(logRecord.getLogId());
        accManifestRecord.update(ACC_MANIFEST.LOG_ID);

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
        LogRecord logRecord =
                logRepository.insertAccLog(accManifestRecord,
                        accRecord, accManifestRecord.getLogId(),
                        LogAction.Deleted,
                        userId, timestamp);

        accManifestRecord.setLogId(logRecord.getLogId());
        accManifestRecord.update(ACC_MANIFEST.LOG_ID);

        return new DeleteAccRepositoryResponse(accManifestRecord.getAccManifestId().toBigInteger());
    }

    public DeleteAccRepositoryResponse removeAcc(DeleteAccRepositoryRequest request) {
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

        if (dslContext.selectCount()
                .from(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                .fetchOneInto(Long.class) == 0) {
            if (dslContext.selectCount()
                    .from(ASCCP_MANIFEST)
                    .where(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                    .fetchOneInto(Long.class) == 0) {
                accManifestRecord.delete();
            }
        }

        if (dslContext.selectCount()
                .from(ASCC)
                .where(ASCC.FROM_ACC_ID.eq(accRecord.getAccId()))
                .fetchOneInto(Long.class) == 0) {
            if (dslContext.selectCount()
                    .from(ASCCP)
                    .where(ASCCP.ROLE_OF_ACC_ID.eq(accRecord.getAccId()))
                    .fetchOneInto(Long.class) == 0) {
                accRecord.delete();
            }
        }

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

        LogRecord logRecord =
                logRepository.insertAccLog(accManifestRecord,
                        accRecord, accManifestRecord.getLogId(),
                        LogAction.Modified,
                        userId, timestamp);

        accManifestRecord.setLogId(logRecord.getLogId());
        accManifestRecord.update(ACC_MANIFEST.LOG_ID);

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

        moveSeq(request.getUser(), accRecord, accManifestRecord,
                request.getItem(), request.getAfter());

        LogRecord logRecord =
                logRepository.insertAccLog(accManifestRecord,
                        accRecord, accManifestRecord.getLogId(),
                        LogAction.Modified,
                        userId, timestamp);

        accManifestRecord.setLogId(logRecord.getLogId());
        accManifestRecord.update(ACC_MANIFEST.LOG_ID);
    }

    public void moveSeq(AuthenticatedPrincipal requester, AccRecord accRecord, AccManifestRecord accManifestRecord,
                        CcId item, CcId after) {
        AppUser user = sessionService.getAppUser(requester);
        ULong userId = ULong.valueOf(user.getAppUserId());
        SeqKeyHandler seqKeyHandler;

        if (!accRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        switch (item.getType().toLowerCase()) {
            case "asccp":
                AsccManifestRecord asccManifestRecord = getAsccManifestRecordForUpdateSeq(accManifestRecord, item);
                seqKeyHandler = seqKeyHandler(requester, asccManifestRecord);
                break;

            case "bccp":
                BccManifestRecord bccManifestRecord = getBccManifestRecordForUpdateSeq(accManifestRecord, item);
                seqKeyHandler = seqKeyHandler(requester, bccManifestRecord);
                break;

            default:
                throw new IllegalArgumentException();
        }

        if (after == null) {
            seqKeyHandler.moveTo(MoveTo.FIRST);
        } else {
            SeqKey seqKey;
            switch (after.getType().toLowerCase()) {
                case "asccp":
                    AsccManifestRecord asccManifestRecord = getAsccManifestRecordForUpdateSeq(accManifestRecord, after);
                    seqKey = scoreRepositoryFactory.createSeqKeyReadRepository()
                            .getSeqKey(new GetSeqKeyRequest(sessionService.asScoreUser(requester))
                                    .withSeqKeyId(asccManifestRecord.getSeqKeyId().toBigInteger()))
                            .getSeqKey();
                    break;

                case "bccp":
                    BccManifestRecord bccManifestRecord = getBccManifestRecordForUpdateSeq(accManifestRecord, after);
                    seqKey = scoreRepositoryFactory.createSeqKeyReadRepository()
                            .getSeqKey(new GetSeqKeyRequest(sessionService.asScoreUser(requester))
                                    .withSeqKeyId(bccManifestRecord.getSeqKeyId().toBigInteger()))
                            .getSeqKey();
                    break;

                default:
                    throw new IllegalArgumentException();
            }

            seqKeyHandler.moveAfter(seqKey);
        }
    }

    private AsccManifestRecord getAsccManifestRecordForUpdateSeq(AccManifestRecord accManifestRecord, CcId ccId) {
        AsccManifestRecord asccManifestRecord = dslContext
                .selectFrom(ASCC_MANIFEST)
                .where(
                        and(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ULong.valueOf(ccId.getManifestId())),
                                ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                )
                .fetchOneInto(AsccManifestRecord.class);

        
        if (!asccManifestRecord.getFromAccManifestId().equals(accManifestRecord.getAccManifestId())) {
            throw new IllegalArgumentException("It only allows to modify the core component for the corresponding component.");
        }

        return asccManifestRecord;
    }

    private BccManifestRecord getBccManifestRecordForUpdateSeq(AccManifestRecord accManifestRecord, CcId ccId) {
        BccManifestRecord bccManifestRecord = dslContext
                .selectFrom(BCC_MANIFEST)
                .where(
                        and(BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(ULong.valueOf(ccId.getManifestId())),
                                BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                )
                .fetchOneInto(BccManifestRecord.class);


        if (!bccManifestRecord.getFromAccManifestId().equals(accManifestRecord.getAccManifestId())) {
            throw new IllegalArgumentException("It only allows to modify the core component for the corresponding component.");
        }

        return bccManifestRecord;
    }

    public CancelRevisionAccRepositoryResponse cancelRevisionAcc(CancelRevisionAccRepositoryRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(request.getUser()));
        LocalDateTime timestamp = request.getLocalDateTime();

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

        AccRecord prevAccRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accRecord.getPrevAccId())).fetchOne();

        // creates new revision for canceled record.
        LogRecord logRecord =
                logRepository.insertAccLog(accManifestRecord,
                        prevAccRecord, accManifestRecord.getLogId(),
                        LogAction.Canceled,
                        userId, timestamp);

        // update ACC MANIFEST's acc_id and revision_id
        if (prevAccRecord.getBasedAccId() != null) {
            String prevBasedAccGuid = dslContext.select(ACC.GUID)
                    .from(ACC).where(ACC.ACC_ID.eq(prevAccRecord.getBasedAccId())).fetchOneInto(String.class);
            AccManifestRecord basedAccManifest = dslContext.select(ACC_MANIFEST.fields()).from(ACC)
                    .join(ACC_MANIFEST).on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                    .where(and(ACC_MANIFEST.RELEASE_ID.eq(accManifestRecord.getReleaseId()),
                            ACC.GUID.eq(prevBasedAccGuid))).fetchOneInto(AccManifestRecord.class);
            accManifestRecord.setBasedAccManifestId(basedAccManifest.getAccManifestId());
        }
        accManifestRecord.setAccId(accRecord.getPrevAccId());
        accManifestRecord.setLogId(logRecord.getLogId());
        accManifestRecord.update(ACC_MANIFEST.ACC_ID, ACC_MANIFEST.LOG_ID, ACC_MANIFEST.BASED_ACC_MANIFEST_ID);

        discardLogAssociations(accManifestRecord, accRecord);

        // unlink prev ACC
        prevAccRecord.setNextAccId(null);
        prevAccRecord.update(ACC.NEXT_ACC_ID);

        // delete current ACC
        accRecord.delete();

        return new CancelRevisionAccRepositoryResponse(request.getAccManifestId());
    }

    private void discardLogAssociations(AccManifestRecord accManifestRecord, AccRecord accRecord) {
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
                .where(SEQ_KEY.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId())).execute();
        dslContext.deleteFrom(SEQ_KEY).where(SEQ_KEY.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId())).execute();
    }

    public CancelRevisionAccRepositoryResponse resetRevisionAcc(CancelRevisionAccRepositoryRequest request) {
        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(request.getAccManifestId()))).fetchOne();

        if (accManifestRecord == null) {
            throw new IllegalArgumentException("Not found a target ACC");
        }

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();

        LogRecord cursorLog = dslContext.selectFrom(LOG)
                .where(LOG.LOG_ID.eq(accManifestRecord.getLogId())).fetchOne();

        UInteger revisionNum = cursorLog.getRevisionNum();

        if (cursorLog.getPrevLogId() == null) {
            throw new IllegalArgumentException("There is no change to be reset.");
        }

        List<ULong> deleteLogTargets = new ArrayList<>();

        while (cursorLog.getPrevLogId() != null) {
            if (!cursorLog.getRevisionNum().equals(revisionNum)) {
                throw new IllegalArgumentException("Cannot find reset point");
            }
            if (cursorLog.getRevisionTrackingNum().equals(UInteger.valueOf(1))) {
                break;
            }
            deleteLogTargets.add(cursorLog.getLogId());
            cursorLog = dslContext.selectFrom(LOG)
                    .where(LOG.LOG_ID.eq(cursorLog.getPrevLogId())).fetchOne();
        }

        JsonObject snapshot = serializer.deserialize(cursorLog.getSnapshot().toString());

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
        accManifestRecord.setLogId(cursorLog.getLogId());
        accManifestRecord.update(ACC_MANIFEST.BASED_ACC_MANIFEST_ID, ACC_MANIFEST.LOG_ID);

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

        cursorLog.setNextLogId(null);
        cursorLog.update(LOG.NEXT_LOG_ID);
        dslContext.update(LOG)
                .setNull(LOG.PREV_LOG_ID)
                .setNull(LOG.NEXT_LOG_ID)
                .where(LOG.LOG_ID.in(deleteLogTargets))
                .execute();
        dslContext.deleteFrom(LOG).where(LOG.LOG_ID.in(deleteLogTargets)).execute();

        return new CancelRevisionAccRepositoryResponse(request.getAccManifestId());
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

    private SeqKeyHandler seqKeyHandler(AuthenticatedPrincipal user, AsccManifestRecord asccManifestRecord) {
        SeqKeyHandler seqKeyHandler = new SeqKeyHandler(scoreRepositoryFactory,
                sessionService.asScoreUser(user));
        seqKeyHandler.initAscc(
                asccManifestRecord.getFromAccManifestId().toBigInteger(),
                (asccManifestRecord.getSeqKeyId() != null) ? asccManifestRecord.getSeqKeyId().toBigInteger() : null,
                asccManifestRecord.getAsccManifestId().toBigInteger());
        return seqKeyHandler;
    }

    private SeqKeyHandler seqKeyHandler(AuthenticatedPrincipal user, BccManifestRecord asccManifestRecord) {
        SeqKeyHandler seqKeyHandler = new SeqKeyHandler(scoreRepositoryFactory,
                sessionService.asScoreUser(user));
        seqKeyHandler.initBcc(
                asccManifestRecord.getFromAccManifestId().toBigInteger(),
                (asccManifestRecord.getSeqKeyId() != null) ? asccManifestRecord.getSeqKeyId().toBigInteger() : null,
                asccManifestRecord.getBccManifestId().toBigInteger());
        return seqKeyHandler;
    }

}