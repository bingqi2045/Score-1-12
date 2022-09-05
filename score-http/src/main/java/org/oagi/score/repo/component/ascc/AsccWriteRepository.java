package org.oagi.score.repo.component.ascc;

import org.jooq.DSLContext;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.oagi.score.gateway.http.api.cc_management.data.CcASCCPType;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.helper.ScoreGuid;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.service.common.data.AppUser;
import org.oagi.score.service.common.data.CcState;
import org.oagi.score.service.corecomponent.seqkey.MoveTo;
import org.oagi.score.service.corecomponent.seqkey.SeqKeyHandler;
import org.oagi.score.service.log.LogRepository;
import org.oagi.score.service.log.model.LogAction;
import org.oagi.score.service.log.model.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.compare;
import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.Acc.ACC;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.AccManifest.ACC_MANIFEST;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.Ascc.ASCC;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.AsccManifest.ASCC_MANIFEST;

@Repository
public class AsccWriteRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    private void ensureNoConflictInForward(AccManifestRecord fromAccManifestRecord, AsccpRecord asccpRecord) {
        // Check conflicts in forward
        AccManifestRecord basedAccManifestRecord = fromAccManifestRecord;
        while (basedAccManifestRecord != null) {
            String accDen = dslContext.select(ACC.DEN)
                    .from(ASCC_MANIFEST)
                    .join(ACC_MANIFEST).on(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                    .join(ACC).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                    .join(ASCCP_MANIFEST).on(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                    .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                    .where(and(
                            ASCC_MANIFEST.RELEASE_ID.eq(basedAccManifestRecord.getReleaseId()),
                            ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(basedAccManifestRecord.getAccManifestId()),
                            ASCCP.ASCCP_ID.eq(asccpRecord.getAsccpId())
                    ))
                    .fetchOptionalInto(String.class).orElse(null);
            if (accDen != null) {
                throw new IllegalArgumentException("ACC [" + accDen + "] already has ASCCP [" + asccpRecord.getDen() + "]");
            }

            if (basedAccManifestRecord.getBasedAccManifestId() != null) {
                basedAccManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                        .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(basedAccManifestRecord.getBasedAccManifestId())).fetchOne();
            } else {
                basedAccManifestRecord = null;
            }
        }

        // Check conflicts in backward
        List<AccManifestRecord> childAccManifestRecords = new ArrayList();
        childAccManifestRecords.addAll(
                dslContext.selectFrom(ACC_MANIFEST)
                        .where(ACC_MANIFEST.BASED_ACC_MANIFEST_ID.eq(fromAccManifestRecord.getAccManifestId()))
                        .fetchInto(AccManifestRecord.class)
        );
    }

    private void ensureNoConflictInBackward(AccManifestRecord fromAccManifestRecord, AsccpRecord asccpRecord) {
        List<AccManifestRecord> childAccManifestRecords = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.BASED_ACC_MANIFEST_ID.eq(fromAccManifestRecord.getAccManifestId()))
                .fetchInto(AccManifestRecord.class);
        if (childAccManifestRecords.isEmpty()) {
            return;
        }

        for (AccManifestRecord childAccManifestRecord : childAccManifestRecords) {
            String accDen = dslContext.select(ACC.DEN)
                    .from(ASCC_MANIFEST)
                    .join(ACC_MANIFEST).on(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                    .join(ACC).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                    .join(ASCCP_MANIFEST).on(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                    .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                    .where(and(
                            ASCC_MANIFEST.RELEASE_ID.eq(childAccManifestRecord.getReleaseId()),
                            ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(childAccManifestRecord.getAccManifestId()),
                            ASCCP.ASCCP_ID.eq(asccpRecord.getAsccpId())
                    ))
                    .fetchOptionalInto(String.class).orElse(null);
            if (accDen != null) {
                throw new IllegalArgumentException("ACC [" + accDen + "] already has ASCCP [" + asccpRecord.getDen() + "]");
            }

            ensureNoConflictInBackward(childAccManifestRecord, asccpRecord);
        }
    }

    public CreateAsccRepositoryResponse createAscc(CreateAsccRepositoryRequest request) {
        String userId = sessionService.userId(request.getUser());
        LocalDateTime timestamp = request.getLocalDateTime();

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(and(
                        ACC_MANIFEST.RELEASE_ID.eq(request.getReleaseId()),
                        ACC_MANIFEST.ACC_MANIFEST_ID.eq(request.getAccManifestId())
                ))
                .fetchOne();

        if (accManifestRecord == null) {
            throw new IllegalArgumentException("Source ACC does not exist.");
        }

        AsccpManifestRecord asccpManifestRecord = dslContext.selectFrom(ASCCP_MANIFEST)
                .where(and(
                        ASCCP_MANIFEST.RELEASE_ID.eq(request.getReleaseId()),
                        ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(request.getAsccpManifestId())
                ))
                .fetchOne();

        AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId())).fetchOne();

        if (asccpManifestRecord == null) {
            throw new IllegalArgumentException("Target ASCCP does not exist.");
        }

        ensureNoConflictInForward(accManifestRecord, asccpRecord);
        ensureNoConflictInBackward(accManifestRecord, asccpRecord);

        if (dslContext.selectCount()
                .from(ASCCP_MANIFEST)
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .join(ASCC_MANIFEST).on(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID))
                .where(and(ASCCP.REUSABLE_INDICATOR.eq((byte) 0),
                        ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(request.getAsccpManifestId()))
                )
                .fetchOneInto(Integer.class) > 0) {
            throw new IllegalArgumentException("Target ASCCP is not reusable.");
        }

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();

        if (asccpRecord.getType().equals(CcASCCPType.Extension.name())) {
            if (dslContext.selectCount()
                    .from(ASCCP_MANIFEST)
                    .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                    .join(ASCC_MANIFEST).on(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID))
                    .where(and(ASCCP.TYPE.eq(CcASCCPType.Extension.name()),
                            ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(request.getAsccpManifestId()))
                    )
                    .fetchOneInto(Integer.class) > 0) {
                throw new IllegalArgumentException("This ACC already has Extension ASCCP.");
            }
        }

        AsccRecord ascc = new AsccRecord();
        ascc.setAsccId(UUID.randomUUID().toString());
        ascc.setGuid(ScoreGuid.randomGuid());
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
        dslContext.insertInto(ASCC)
                .set(ascc)
                .execute();

        AsccManifestRecord asccManifest = new AsccManifestRecord();
        asccManifest.setAsccManifestId(UUID.randomUUID().toString());
        asccManifest.setAsccId(ascc.getAsccId());
        asccManifest.setReleaseId(request.getReleaseId());
        asccManifest.setFromAccManifestId(accManifestRecord.getAccManifestId());
        asccManifest.setToAsccpManifestId(asccpManifestRecord.getAsccpManifestId());

        dslContext.insertInto(ASCC_MANIFEST)
                .set(asccManifest)
                .execute();

        seqKeyHandler(request.getUser(), asccManifest).moveTo(request.getPos());

        if (request.getLogAction() != null) {
            upsertLogIntoAccAndAssociationsByAction(
                    accRecord, accManifestRecord,
                    request.getReleaseId(),
                    userId, timestamp, request.getLogHash(), request.getLogAction()
            );
        } else {
            upsertLogIntoAccAndAssociations(
                    accRecord, accManifestRecord,
                    request.getReleaseId(),
                    userId, timestamp
            );
        }


        return new CreateAsccRepositoryResponse(asccManifest.getAsccManifestId());
    }

    private void upsertLogIntoAccAndAssociations(AccRecord accRecord,
                                                 AccManifestRecord accManifestRecord,
                                                 String releaseId,
                                                 String userId, LocalDateTime timestamp) {
        upsertLogIntoAccAndAssociationsByAction(accRecord, accManifestRecord, releaseId, userId, timestamp, LogUtils.generateHash(), LogAction.Modified);
    }

    private void upsertLogIntoAccAndAssociationsByAction(AccRecord accRecord,
                                                         AccManifestRecord accManifestRecord,
                                                         String releaseId,
                                                         String userId, LocalDateTime timestamp,
                                                         String hash, LogAction action) {
        LogRecord logRecord =
                logRepository.insertAccLog(accManifestRecord,
                        accRecord, accManifestRecord.getLogId(),
                        action, userId, timestamp, hash);

        accManifestRecord.setLogId(logRecord.getLogId());
        accManifestRecord.update(ACC_MANIFEST.LOG_ID);
    }

    public UpdateAsccPropertiesRepositoryResponse updateAsccProperties(UpdateAsccPropertiesRepositoryRequest request) {
        AppUser user = sessionService.getAppUserByUsername(request.getUser());
        String userId = user.getAppUserId();
        LocalDateTime timestamp = request.getLocalDateTime();

        AsccManifestRecord asccManifestRecord = dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(
                        request.getAsccManifestId()
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

        if (!request.isPropagation() && !CcState.WIP.equals(CcState.valueOf(accRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
        }

        if (!request.isPropagation() && !accRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update ascc record.
        UpdateSetFirstStep<AsccRecord> firstStep = dslContext.update(ASCC);
        UpdateSetMoreStep<AsccRecord> moreStep = null;

        String den = accRecord.getObjectClassTerm() + ". " + dslContext.select(ASCCP.DEN)
                .from(ASCCP)
                .join(ASCCP_MANIFEST).on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(asccManifestRecord.getToAsccpManifestId()))
                .fetchOneInto(String.class);
        if (compare(asccRecord.getDen(), den) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ASCC.DEN, den);
        }
        if (compare(asccRecord.getDefinition(), request.getDefinition()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ASCC.DEFINITION, request.getDefinition());
        }
        if (compare(asccRecord.getDefinition(), request.getDefinitionSource()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ASCC.DEFINITION_SOURCE, request.getDefinitionSource());
        }
        if ((asccRecord.getIsDeprecated() == 1) != request.isDeprecated()) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ASCC.IS_DEPRECATED, (byte) ((request.isDeprecated()) ? 1 : 0));
        }
        if (request.getCardinalityMin() != null && asccRecord.getCardinalityMin() != request.getCardinalityMin()) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ASCC.CARDINALITY_MIN, request.getCardinalityMin());
        }
        if (request.getCardinalityMax() != null && asccRecord.getCardinalityMax() != request.getCardinalityMax()) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ASCC.CARDINALITY_MAX, request.getCardinalityMax());
        }

        if (moreStep != null) {
            moreStep.set(ASCC.LAST_UPDATED_BY, userId)
                    .set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp)
                    .where(ASCC.ASCC_ID.eq(asccRecord.getAsccId()))
                    .execute();

            asccRecord = dslContext.selectFrom(ASCC)
                    .where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId()))
                    .fetchOne();

            upsertLogIntoAccAndAssociations(
                    accRecord, accManifestRecord,
                    accManifestRecord.getReleaseId(),
                    userId, timestamp
            );
        }

        return new UpdateAsccPropertiesRepositoryResponse(asccManifestRecord.getAsccManifestId());
    }

    public DeleteAsccRepositoryResponse deleteAscc(DeleteAsccRepositoryRequest request) {
        AppUser user = sessionService.getAppUserByUsername(request.getUser());
        String userId = user.getAppUserId();
        LocalDateTime timestamp = request.getLocalDateTime();

        AsccManifestRecord asccManifestRecord = dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(
                        request.getAsccManifestId()
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

        if (!request.isIgnoreState()) {
            if (!CcState.WIP.equals(CcState.valueOf(accRecord.getState()))) {
                throw new IllegalArgumentException("Only the core component in 'WIP' state can be deleted.");
            }

            if (!accRecord.getOwnerUserId().equals(userId)) {
                throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
            }
        }

        int usedBieCount = dslContext.selectCount().from(ASBIE)
                .where(ASBIE.BASED_ASCC_MANIFEST_ID.eq(asccManifestRecord.getAsccManifestId())).fetchOne(0, int.class);

        if (usedBieCount > 0) {
            throw new IllegalArgumentException("This association used in " + usedBieCount + " BIE(s). Can not be deleted.");
        }

        // delete from Tables
        seqKeyHandler(request.getUser(), asccManifestRecord).deleteCurrent();
        dslContext.update(ASCC_MANIFEST).setNull(ASCC_MANIFEST.NEXT_ASCC_MANIFEST_ID)
                .where(ASCC_MANIFEST.NEXT_ASCC_MANIFEST_ID.eq(asccManifestRecord.getAsccManifestId())).execute();
        dslContext.update(ASCC_MANIFEST).setNull(ASCC_MANIFEST.PREV_ASCC_MANIFEST_ID)
                .where(ASCC_MANIFEST.PREV_ASCC_MANIFEST_ID.eq(asccManifestRecord.getAsccManifestId())).execute();
        asccManifestRecord.delete();
        if (dslContext.selectCount().from(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.ASCC_ID.eq(asccManifestRecord.getAsccId())).fetchOne(0, int.class) == 0) {
            dslContext.update(ASCC).setNull(ASCC.NEXT_ASCC_ID)
                    .where(ASCC.NEXT_ASCC_ID.eq(asccRecord.getAsccId())).execute();
            dslContext.update(ASCC).setNull(ASCC.PREV_ASCC_ID)
                    .where(ASCC.PREV_ASCC_ID.eq(asccRecord.getAsccId())).execute();
            asccRecord.delete();
        }

        if (request.getLogAction() != null) {
            upsertLogIntoAccAndAssociationsByAction(
                    accRecord, accManifestRecord,
                    accManifestRecord.getReleaseId(),
                    userId, timestamp, request.getLogHash(), request.getLogAction()
            );
        } else {
            upsertLogIntoAccAndAssociations(
                    accRecord, accManifestRecord,
                    accManifestRecord.getReleaseId(),
                    userId, timestamp
            );
        }

        return new DeleteAsccRepositoryResponse(asccManifestRecord.getAsccManifestId());
    }

    private SeqKeyHandler seqKeyHandler(AuthenticatedPrincipal user, AsccManifestRecord asccManifestRecord) {
        SeqKeyHandler seqKeyHandler = new SeqKeyHandler(scoreRepositoryFactory,
                sessionService.asScoreUser(user));
        seqKeyHandler.initAscc(
                asccManifestRecord.getFromAccManifestId(),
                (asccManifestRecord.getSeqKeyId() != null) ? asccManifestRecord.getSeqKeyId() : null,
                asccManifestRecord.getAsccManifestId());
        return seqKeyHandler;
    }

    public RefactorAsccRepositoryResponse refactor(RefactorAsccRepositoryRequest request) {
        AppUser user = sessionService.getAppUserByUsername(request.getUser());
        String userId = user.getAppUserId();
        LocalDateTime timestamp = request.getLocalDateTime();

        AsccManifestRecord targetAsccManifestRecord = dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(
                        request.getAsccManifestId()
                ))
                .fetchOne();

        AccManifestRecord targetAccManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(request.getAccManifestId()))
                .fetchOne();

        AsccRecord targetAsccRecord = dslContext.selectFrom(ASCC).where(ASCC.ASCC_ID.eq(targetAsccManifestRecord.getAsccId())).fetchOne();

        String asccpDen = dslContext.select(ASCCP.DEN)
                .from(ASCCP_MANIFEST)
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(targetAsccManifestRecord.getToAsccpManifestId()))
                .fetchOneInto(String.class);

        AccRecord targetAccRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(targetAccManifestRecord.getAccId()))
                .fetchOne();

        List<AsccManifestRecord> targetAsccManifestList = this.getRefactorTargetAsccManifestList(targetAsccManifestRecord, targetAccManifestRecord.getAccManifestId());

        String hash = LogUtils.generateHash();

        for (AsccManifestRecord asccManifestRecord: targetAsccManifestList) {

            AsccRecord asccRecord = dslContext.selectFrom(ASCC)
                    .where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId()))
                    .fetchOne();

            AccManifestRecord prevAccManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                    .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(asccManifestRecord.getFromAccManifestId()))
                    .fetchOne();

            AccRecord prevAccRecord = dslContext.selectFrom(ACC)
                    .where(ACC.ACC_ID.eq(prevAccManifestRecord.getAccId()))
                    .fetchOne();

            AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                    .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(request.getAccManifestId()))
                    .fetchOne();

            AccRecord accRecord = dslContext.selectFrom(ACC)
                    .where(ACC.ACC_ID.eq(accManifestRecord.getAccId()))
                    .fetchOne();

            if (!CcState.WIP.equals(CcState.valueOf(accRecord.getState()))) {
                throw new IllegalArgumentException("Only the core component in 'WIP' state can be refactored.");
            }

            if (!accRecord.getOwnerUserId().equals(userId)) {
                throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
            }

            int usedBieCount = dslContext.selectCount().from(ASBIE)
                    .where(ASBIE.BASED_ASCC_MANIFEST_ID.eq(asccManifestRecord.getAsccManifestId())).fetchOne(0, int.class);

            if (usedBieCount > 0) {
                throw new IllegalArgumentException("This association used in " + usedBieCount + " BIE(s). Can not be refactored.");
            }

            seqKeyHandler(request.getUser(), asccManifestRecord).deleteCurrent();
            dslContext.update(ASCC_MANIFEST)
                    .setNull(ASCC_MANIFEST.NEXT_ASCC_MANIFEST_ID)
                    .where(ASCC_MANIFEST.NEXT_ASCC_MANIFEST_ID.eq(asccManifestRecord.getAsccManifestId()))
                    .execute();
            dslContext.update(ASCC_MANIFEST)
                    .setNull(ASCC_MANIFEST.PREV_ASCC_MANIFEST_ID)
                    .where(ASCC_MANIFEST.PREV_ASCC_MANIFEST_ID.eq(asccManifestRecord.getAsccManifestId()))
                    .execute();
            asccManifestRecord.delete();

            dslContext.update(ASCC)
                    .setNull(ASCC.NEXT_ASCC_ID)
                    .where(ASCC.NEXT_ASCC_ID.eq(asccRecord.getAsccId()))
                    .execute();
            dslContext.update(ASCC)
                    .setNull(ASCC.PREV_ASCC_ID)
                    .where(ASCC.PREV_ASCC_ID.eq(asccRecord.getAsccId()))
                    .execute();
            asccRecord.delete();

            upsertLogIntoAccAndAssociationsByAction(
                    prevAccRecord, prevAccManifestRecord,
                    accManifestRecord.getReleaseId(),
                    userId, timestamp, hash, LogAction.Refactored
            );
        }

        targetAsccRecord.setAsccId(UUID.randomUUID().toString());
        targetAsccRecord.setLastUpdatedBy(userId);
        targetAsccRecord.setLastUpdateTimestamp(timestamp);
        targetAsccRecord.setFromAccId(targetAccRecord.getAccId());
        targetAsccRecord.setPrevAsccId(null);
        targetAsccRecord.setNextAsccId(null);
        targetAsccRecord.setDen(targetAccRecord.getObjectClassTerm() + ". " + asccpDen);
        dslContext.insertInto(ASCC).set(targetAsccRecord).execute();
        String asccId = targetAsccRecord.getAsccId();

        targetAsccManifestRecord.setAsccManifestId(UUID.randomUUID().toString());
        targetAsccManifestRecord.setFromAccManifestId(request.getAccManifestId());
        targetAsccManifestRecord.setAsccId(asccId);
        targetAsccManifestRecord.setSeqKeyId(null);
        targetAsccManifestRecord.setPrevAsccManifestId(null);
        targetAsccManifestRecord.setNextAsccManifestId(null);
        dslContext.insertInto(ASCC_MANIFEST).set(targetAsccManifestRecord).execute();

        seqKeyHandler(request.getUser(), targetAsccManifestRecord).moveTo(MoveTo.LAST);

        upsertLogIntoAccAndAssociationsByAction(
                targetAccRecord, targetAccManifestRecord,
                targetAccManifestRecord.getReleaseId(),
                userId, timestamp, hash, LogAction.Ungrouped
        );

        return new RefactorAsccRepositoryResponse(targetAsccManifestRecord.getAsccManifestId());
    }

    private List<AsccManifestRecord> getRefactorTargetAsccManifestList(AsccManifestRecord asccManifestRecord, String targetAccManifestId) {
        String releaseId = asccManifestRecord.getReleaseId();
        List<AccManifestRecord> accManifestList = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.RELEASE_ID.eq(releaseId)).fetch();
        Map<String, List<AccManifestRecord>> baseAccMap = accManifestList.stream().filter(e -> e.getBasedAccManifestId() != null)
                .collect(Collectors.groupingBy(AccManifestRecord::getBasedAccManifestId));

        List<AsccManifestRecord> asccList = dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.RELEASE_ID.eq(releaseId)).fetch();
        Map<String, List<AsccManifestRecord>> fromAccAsccMap = asccList.stream()
                .collect(Collectors.groupingBy(AsccManifestRecord::getFromAccManifestId));

        List<String> accManifestIdList = new ArrayList<>();

        accManifestIdList.add(targetAccManifestId);

        Set<String> accCandidates = new HashSet<>();

        for (String cur : accManifestIdList) {
            accCandidates.addAll(getBaseAccManifestId(cur, baseAccMap));
        }

        Set<AsccManifestRecord> asccResult = new HashSet<>();

        for (String acc : accCandidates) {
            asccResult.addAll(
                    fromAccAsccMap.getOrDefault(acc, Collections.emptyList())
                            .stream()
                            .filter(ascc -> ascc.getToAsccpManifestId().equals(asccManifestRecord.getToAsccpManifestId()))
                            .collect(Collectors.toList()));
        }
        return new ArrayList<>(asccResult);
    }

    private List<String> getBaseAccManifestId(String accManifestId, Map<String, List<AccManifestRecord>> baseAccMap) {
        List<String> result = new ArrayList<>();
        result.add(accManifestId);
        if (baseAccMap.containsKey(accManifestId)) {
            baseAccMap.get(accManifestId).forEach(e -> {
                result.addAll(getBaseAccManifestId(e.getAccManifestId(), baseAccMap));
            });
        }
        return result;
    }
}