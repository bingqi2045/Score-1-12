package org.oagi.score.repo.component.acc;

import com.google.gson.*;
import net.sf.saxon.expr.BigRangeIterator;
import org.jooq.*;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AsccManifest;
import org.oagi.score.service.common.data.AppUser;
import org.oagi.score.service.common.data.BCCEntityType;
import org.oagi.score.service.log.model.LogAction;
import org.oagi.score.service.common.data.OagisComponentType;
import org.oagi.score.gateway.http.api.cc_management.data.CcId;
import org.oagi.score.service.common.data.CcState;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.helper.ScoreGuid;
import org.oagi.score.service.log.LogRepository;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.corecomponent.seqkey.SeqKeyWriteRepository;
import org.oagi.score.repo.api.corecomponent.seqkey.model.*;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.service.log.model.LogSerializer;
import org.oagi.score.service.corecomponent.seqkey.MoveTo;
import org.oagi.score.service.corecomponent.seqkey.SeqKeyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang3.StringUtils.compare;
import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.val;
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

            bccManifestRecord.setBccId(nextBccRecord.getBccId());
            bccManifestRecord.setFromAccManifestId(accManifestRecord.getAccManifestId());
            bccManifestRecord.update(BCC_MANIFEST.BCC_ID, BCC_MANIFEST.FROM_ACC_MANIFEST_ID);
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
        if (!prevState.canForceMove(request.getToState())) {
            accRecord.setLastUpdatedBy(userId);
            accRecord.setLastUpdateTimestamp(timestamp);
        }
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

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(request.getAccManifestId()))).fetchOne();

        if (accManifestRecord == null) {
            throw new IllegalArgumentException("Not found a target ACC");
        }

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();

        AccRecord prevAccRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accRecord.getPrevAccId())).fetchOne();

        if (prevAccRecord == null) {
            throw new IllegalArgumentException("Not found previous revision");
        }

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
        accManifestRecord.update(ACC_MANIFEST.ACC_ID, ACC_MANIFEST.BASED_ACC_MANIFEST_ID);

        discardLogAssociations(request.getUser(), accManifestRecord, accRecord);

        // unlink prev ACC
        prevAccRecord.setNextAccId(null);
        prevAccRecord.update(ACC.NEXT_ACC_ID);

        // clean logs up
        logRepository.revertToStableState(accManifestRecord);

        // delete current ACC
        accRecord.delete();

        return new CancelRevisionAccRepositoryResponse(request.getAccManifestId());
    }

    private ULong discardLogAssociations(AuthenticatedPrincipal user, AccManifestRecord accManifestRecord, AccRecord accRecord) {
        List<AsccManifestRecord> asccManifestRecords = dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId())).fetch();

        List<BccManifestRecord> bccManifestRecords = dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId())).fetch();

        LogRecord currentLog = dslContext.selectFrom(LOG)
                .where(and(LOG.REFERENCE.eq(accRecord.getGuid()), LOG.LOG_ACTION.eq(LogAction.Revised.name())))
                .orderBy(LOG.LOG_ID.desc()).limit(1).fetchOne();

        List<AsccManifestRecord> allAsccManifestRecordList = dslContext.selectFrom(ASCC_MANIFEST).fetch();
        Map<ULong, AsccManifestRecord> prevAsccManifestMap = allAsccManifestRecordList.stream()
                .filter(e -> e.getPrevAsccManifestId() != null)
                .collect(Collectors.toMap(AsccManifestRecord::getPrevAsccManifestId, Function.identity()));

        Map<ULong, AsccManifestRecord> nextAsccManifestMap = allAsccManifestRecordList.stream()
                .filter(e -> e.getNextAsccManifestId() != null)
                .collect(Collectors.toMap(AsccManifestRecord::getNextAsccManifestId, Function.identity()));

        List<BccManifestRecord> allBccManifestRecordList = dslContext.selectFrom(BCC_MANIFEST).fetch();
        Map<ULong, BccManifestRecord> prevBccManifestMap = allBccManifestRecordList.stream()
                .filter(e -> e.getPrevBccManifestId() != null)
                .collect(Collectors.toMap(BccManifestRecord::getPrevBccManifestId, Function.identity()));

        Map<ULong, BccManifestRecord> nextBccManifestMap = allBccManifestRecordList.stream()
                .filter(e -> e.getNextBccManifestId() != null)
                .collect(Collectors.toMap(BccManifestRecord::getNextBccManifestId, Function.identity()));
        if (currentLog == null || currentLog.getPrevLogId() == null) {
            throw new IllegalArgumentException("Can not found log of revise.");
        }

        dslContext.query("SET FOREIGN_KEY_CHECKS = 0").execute();
        dslContext.deleteFrom(SEQ_KEY).where(SEQ_KEY.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId())).execute();
        dslContext.deleteFrom(ASCC).where(ASCC.ASCC_ID.in(asccManifestRecords.stream().map(AsccManifestRecord::getAsccId).collect(Collectors.toList()))).execute();
        dslContext.deleteFrom(ASCC_MANIFEST).where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId())).execute();
        dslContext.deleteFrom(BCC).where(BCC.BCC_ID.in(bccManifestRecords.stream().map(BccManifestRecord::getBccId).collect(Collectors.toList()))).execute();
        dslContext.deleteFrom(BCC_MANIFEST).where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId())).execute();

        LogRecord prevLog = dslContext.selectFrom(LOG).where(LOG.LOG_ID.eq(currentLog.getPrevLogId())).fetchOne();

        Gson gson = new Gson();
        HashMap obj = gson.fromJson(prevLog.getSnapshot().toString(), HashMap.class);
        Map meta = (Map) obj.get("_metadata");
        List associations = Collections.emptyList();

        if (meta.get("associations") != null) {
            associations = (List) meta.get("associations");
        }

        associations.stream().forEach(association -> {

            Map associationObj = (Map) association;
            if (associationObj.get("ascc") != null) {
                Map asccObj = (Map) associationObj.get("ascc");
                ULong asccId = fromMeta(asccObj, "ascc_id", ULong.class);
                dslContext.deleteFrom(ASCC).where(ASCC.ASCC_ID.eq(asccId)).execute();
                AsccRecord ascc = new AsccRecord();
                ascc.setAsccId(asccId);
                ascc.setGuid(fromMeta(asccObj, "guid", String.class));
                ascc.setCardinalityMax(fromMeta(asccObj, "cardinality_max", Integer.class));
                ascc.setCardinalityMin(fromMeta(asccObj, "cardinality_min", Integer.class));
                ascc.setFromAccId(fromMeta(asccObj, "from_acc_id", ULong.class));
                ascc.setToAsccpId(fromMeta(asccObj, "to_asccp_id", ULong.class));
                ascc.setDefinition(fromMeta(asccObj, "definition", String.class));
                ascc.setDefinitionSource(fromMeta(asccObj, "definition_source", String.class));
                ascc.setIsDeprecated(fromMeta(asccObj, "is_deprecated", Byte.class));
                ascc.setReplacementAsccId(fromMeta(asccObj, "replacement_ascc_id", ULong.class));
                ascc.setCreatedBy(fromMeta(asccObj, "created_by", ULong.class));
                ascc.setOwnerUserId(fromMeta(asccObj, "owner_user_id", ULong.class));
                ascc.setLastUpdatedBy(fromMeta(asccObj, "last_updated_by", ULong.class));
                ascc.setCreationTimestamp(fromMeta(asccObj, "creation_timestamp", LocalDateTime.class));
                ascc.setLastUpdateTimestamp(fromMeta(asccObj, "last_update_timestamp", LocalDateTime.class));
                ascc.setState(fromMeta(asccObj, "state", String.class));
                ascc.setPrevAsccId(fromMeta(asccObj, "prev_ascc_id", ULong.class));
                ascc.setNextAsccId(fromMeta(asccObj, "next_ascc_id", ULong.class));
                ascc.setDen(fromMeta(asccObj, "den", String.class));
                dslContext.insertInto(ASCC).set(ascc).execute();

                Map asccManifestObj = (Map) associationObj.get("asccManifest");
                ULong asccManifestId = fromMeta(asccManifestObj, "ascc_manifest_id", ULong.class);
                dslContext.deleteFrom(ASCC_MANIFEST).where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(asccManifestId)).execute();
                AsccManifestRecord asccManifest = new AsccManifestRecord();
                asccManifest.setAsccManifestId(asccManifestId);
                asccManifest.setAsccId(fromMeta(asccManifestObj, "ascc_id", ULong.class));
                asccManifest.setToAsccpManifestId(fromMeta(asccManifestObj, "to_asccp_manifest_id", ULong.class));
                asccManifest.setFromAccManifestId(fromMeta(asccManifestObj, "from_acc_manifest_id", ULong.class));
                asccManifest.setSeqKeyId(fromMeta(asccManifestObj, "seq_key_id", ULong.class));
                if (nextAsccManifestMap.get(asccManifestId) != null) {
                    asccManifest.setPrevAsccManifestId(nextAsccManifestMap.get(asccManifestId).getAsccManifestId());    
                }
                if (prevAsccManifestMap.get(asccManifestId) != null) {
                    asccManifest.setNextAsccManifestId(prevAsccManifestMap.get(asccManifestId).getAsccManifestId());
                }
                asccManifest.setReplacementAsccManifestId(fromMeta(asccManifestObj, "replacement_ascc_manifest_id", ULong.class));
                asccManifest.setReleaseId(fromMeta(asccManifestObj, "release_id", ULong.class));
                asccManifest.setConflict(fromMeta(asccManifestObj, "conflict", Byte.class));
                dslContext.insertInto(ASCC_MANIFEST).set(asccManifest).execute();

            } else {
                Map bccObj = (Map) associationObj.get("bcc");
                ULong bccId = fromMeta(bccObj, "bcc_id", ULong.class);
                dslContext.deleteFrom(BCC).where(BCC.BCC_ID.eq(bccId)).execute();
                BccRecord bcc = new BccRecord();
                bcc.setBccId(bccId);
                bcc.setGuid(fromMeta(bccObj, "guid", String.class));
                bcc.setCardinalityMax(fromMeta(bccObj, "cardinality_max", Integer.class));
                bcc.setCardinalityMin(fromMeta(bccObj, "cardinality_min", Integer.class));
                bcc.setFromAccId(fromMeta(bccObj, "from_acc_id", ULong.class));
                bcc.setToBccpId(fromMeta(bccObj, "to_bccp_id", ULong.class));
                bcc.setEntityType(fromMeta(bccObj, "entity_type", Integer.class));
                bcc.setDefinition(fromMeta(bccObj, "definition", String.class));
                bcc.setDefinitionSource(fromMeta(bccObj, "definition_source", String.class));
                bcc.setIsDeprecated(fromMeta(bccObj, "is_deprecated", Byte.class));
                bcc.setIsNillable(fromMeta(bccObj, "is_nillable", Byte.class));
                bcc.setReplacementBccId(fromMeta(bccObj, "replacement_bcc_id", ULong.class));
                bcc.setCreatedBy(fromMeta(bccObj, "created_by", ULong.class));
                bcc.setOwnerUserId(fromMeta(bccObj, "owner_user_id", ULong.class));
                bcc.setLastUpdatedBy(fromMeta(bccObj, "last_updated_by", ULong.class));
                bcc.setCreationTimestamp(fromMeta(bccObj, "creation_timestamp", LocalDateTime.class));
                bcc.setLastUpdateTimestamp(fromMeta(bccObj, "last_update_timestamp", LocalDateTime.class));
                bcc.setState(fromMeta(bccObj, "state", String.class));
                bcc.setPrevBccId(fromMeta(bccObj, "prev_bcc_id", ULong.class));
                bcc.setNextBccId(fromMeta(bccObj, "next_bcc_id", ULong.class));
                bcc.setDen(fromMeta(bccObj, "den", String.class));
                bcc.setDefaultValue(fromMeta(bccObj, "default_value", String.class));
                bcc.setFixedValue(fromMeta(bccObj, "fixed_value", String.class));
                dslContext.insertInto(BCC).set(bcc).execute();

                Map bccManifestObj = (Map) associationObj.get("bccManifest");
                ULong bccManifestId = fromMeta(bccManifestObj, "bcc_manifest_id", ULong.class);
                dslContext.deleteFrom(BCC_MANIFEST).where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(bccManifestId)).execute();
                BccManifestRecord bccManifest = new BccManifestRecord();
                bccManifest.setBccManifestId(bccManifestId);
                bccManifest.setBccId(fromMeta(bccManifestObj, "bcc_id", ULong.class));
                bccManifest.setToBccpManifestId(fromMeta(bccManifestObj, "to_bccp_manifest_id", ULong.class));
                bccManifest.setFromAccManifestId(fromMeta(bccManifestObj, "from_acc_manifest_id", ULong.class));
                bccManifest.setSeqKeyId(fromMeta(bccManifestObj, "seq_key_id", ULong.class));
                if (nextBccManifestMap.get(bccManifestId) != null) {
                    bccManifest.setPrevBccManifestId(nextBccManifestMap.get(bccManifestId).getBccManifestId());
                }
                if (prevBccManifestMap.get(bccManifestId) != null) {
                    bccManifest.setNextBccManifestId(prevBccManifestMap.get(bccManifestId).getBccManifestId());
                }
                bccManifest.setReplacementBccManifestId(fromMeta(bccManifestObj, "replacement_bcc_manifest_id", ULong.class));
                bccManifest.setReleaseId(fromMeta(bccManifestObj, "release_id", ULong.class));
                bccManifest.setConflict(fromMeta(bccManifestObj, "conflict", Byte.class));
                dslContext.insertInto(BCC_MANIFEST).set(bccManifest).execute();
            }

            Map seqKeyObj = (Map) associationObj.get("seqKey");
            ULong seqKeyId = fromMeta(seqKeyObj, "seq_key_id", ULong.class);
            dslContext.deleteFrom(SEQ_KEY).where(SEQ_KEY.SEQ_KEY_ID.eq(seqKeyId)).execute();
            SeqKeyRecord seqKey = new SeqKeyRecord();
            seqKey.setSeqKeyId(fromMeta(seqKeyObj, "seq_key_id", ULong.class));
            seqKey.setFromAccManifestId(fromMeta(seqKeyObj, "from_acc_manifest_id", ULong.class));
            seqKey.setAsccManifestId(fromMeta(seqKeyObj, "ascc_manifest_id", ULong.class));
            seqKey.setBccManifestId(fromMeta(seqKeyObj, "bcc_manifest_id", ULong.class));
            seqKey.setPrevSeqKeyId(fromMeta(seqKeyObj, "prev_seq_key_id", ULong.class));
            seqKey.setNextSeqKeyId(fromMeta(seqKeyObj, "next_seq_key_id", ULong.class));
            dslContext.insertInto(SEQ_KEY).set(seqKey).execute();

        });

        dslContext.query("SET FOREIGN_KEY_CHECKS = 1").execute();

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

        return accManifestRecord.getAccManifestId();
    }

    private <T> T fromMeta(Map obj, String key, Class<T> T) {
        Object value = obj.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Double && T == ULong.class) {
            return (T) ULong.valueOf(((Double) value).longValue());
        } else if (value instanceof Double && T == Integer.class) {
            return (T) Integer.valueOf(((Double) value).intValue());
        } else if (value instanceof Boolean) {
            if ((Boolean) value) {
                return (T) Byte.valueOf((byte) 1);
            } else {
                return (T) Byte.valueOf((byte) 0);
            }
        } else if (T == LocalDateTime.class) {
            return (T) LocalDateTime.parse(value.toString());
        }

        return (T) obj.get(key);
    }

    private static class Association {
        public final SeqKeyType type;
        public final ULong manifestId;
        private SeqKeyRecord seqKeyRecord;

        public Association(SeqKeyType type, ULong manifestId) {
            this.type = type;
            this.manifestId = manifestId;
        }

        public ULong getManifestId() {
            return manifestId;
        }

        public SeqKeyRecord getSeqKeyRecord() {
            return seqKeyRecord;
        }

        public void setSeqKeyRecord(SeqKeyRecord seqKeyRecord) {
            this.seqKeyRecord = seqKeyRecord;
        }
    }

    public void insertSeqKey(AuthenticatedPrincipal user, ULong fromAccManifestId, String reference) {

        HashMap<String, Association> associationMap = new HashMap<>();
        dslContext.select(ASCC.GUID, ASCC_MANIFEST.ASCC_MANIFEST_ID)
                .from(ASCC_MANIFEST)
                .join(ASCC).on(ASCC_MANIFEST.ASCC_ID.eq(ASCC.ASCC_ID))
                .where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(fromAccManifestId)).fetch()
                .forEach(e -> {
                    associationMap.put(e.get(ASCC.GUID),
                            new Association(SeqKeyType.ASCC, e.get(ASCC_MANIFEST.ASCC_MANIFEST_ID)));
                });

        dslContext.select(BCC.GUID, BCC_MANIFEST.BCC_MANIFEST_ID)
                .from(BCC_MANIFEST)
                .join(BCC).on(BCC_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                .where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(fromAccManifestId)).fetch()
                .forEach(e -> {
                    associationMap.put(e.get(BCC.GUID),
                            new Association(SeqKeyType.BCC, e.get(BCC_MANIFEST.BCC_MANIFEST_ID)));
                });

        LogRecord log = dslContext.selectFrom(LOG)
                .where(and(LOG.REFERENCE.eq(reference), LOG.LOG_ACTION.eq(LogAction.Revised.name())))
                .orderBy(LOG.LOG_ID.desc())
                .limit(1).fetchOne();

        SeqKeyWriteRepository seqKeyWriteRepository = scoreRepositoryFactory.createSeqKeyWriteRepository();

        JsonObject snapshot = serializer.deserialize(log.getSnapshot().toString());
        JsonArray associations = snapshot.get("associations").getAsJsonArray();
        SeqKey prev = null;
        for (JsonElement obj : associations) {
            String guid = obj.getAsJsonObject().get("guid").getAsString();
            Association association = associationMap.get(guid);
            if (association == null) {
                return;
            }
            CreateSeqKeyRequest request = new CreateSeqKeyRequest(sessionService.asScoreUser(user));
            request.setFromAccManifestId(fromAccManifestId.toBigInteger());
            request.setType(association.type);
            request.setManifestId(association.getManifestId().toBigInteger());
            SeqKey current = seqKeyWriteRepository.createSeqKey(request).getSeqKey();
            if (prev != null) {
                MoveAfterRequest moveAfterRequest = new MoveAfterRequest(sessionService.asScoreUser(user));
                moveAfterRequest.setAfter(prev);
                moveAfterRequest.setItem(current);
                seqKeyWriteRepository.moveAfter(moveAfterRequest);
            }
            prev = current;
        }
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