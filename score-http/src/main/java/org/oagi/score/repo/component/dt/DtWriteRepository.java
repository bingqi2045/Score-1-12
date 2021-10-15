package org.oagi.score.repo.component.dt;

import org.jooq.DSLContext;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.types.ULong;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.helper.ScoreGuid;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CdtAwdPri;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.service.common.data.AppUser;
import org.oagi.score.service.common.data.CcState;
import org.oagi.score.service.log.LogRepository;
import org.oagi.score.service.log.model.LogAction;
import org.oagi.score.service.log.model.LogSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.compare;
import static org.jooq.impl.DSL.*;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Repository
public class DtWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private LogSerializer serializer;

    public CreateBdtRepositoryResponse createBdt(CreateBdtRepositoryRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(request.getUser()));
        LocalDateTime timestamp = request.getLocalDateTime();

        DtManifestRecord basedBdtManifest = dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(request.getBasedDdtManifestId())))
                .fetchOne();

        List<DtScManifestRecord> basedBdtScManifestList = dslContext.selectFrom(DT_SC_MANIFEST)
                .where(DT_SC_MANIFEST.OWNER_DT_MANIFEST_ID.eq(basedBdtManifest.getDtManifestId()))
                .fetch();

        DtRecord basedBdt = dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(basedBdtManifest.getDtId()))
                .fetchOne();

        List<DtScRecord> basedBdtScList = dslContext.selectFrom(DT_SC)
                .where(DT_SC.OWNER_DT_ID.eq(basedBdt.getDtId()))
                .fetch();

        DtRecord bdt = new DtRecord();
        bdt.setGuid(ScoreGuid.randomGuid());
        bdt.setDataTypeTerm(basedBdt.getDataTypeTerm());

        if (basedBdt.getQualifier() != null) {
            bdt.setQualifier(basedBdt.getQualifier() + "_ Qualifier");
            bdt.setDen(bdt.getQualifier() + "_ " + bdt.getDataTypeTerm() + ". Type");

        } else {
            bdt.setQualifier("Qualifier");
            bdt.setDen(bdt.getQualifier() + "_ " + bdt.getDataTypeTerm() + ". Type");
        }

        bdt.setRepresentationTerm(basedBdt.getDataTypeTerm());
        bdt.setBasedDtId(basedBdt.getDtId());
        bdt.setState(CcState.WIP.name());
        bdt.setIsDeprecated((byte) 0);
        bdt.setCommonlyUsed((byte) 1);
        bdt.setNamespaceId(null);
        bdt.setCreatedBy(userId);
        bdt.setLastUpdatedBy(userId);
        bdt.setOwnerUserId(userId);
        bdt.setCreationTimestamp(timestamp);
        bdt.setLastUpdateTimestamp(timestamp);

        bdt.setDtId(
                dslContext.insertInto(DT)
                        .set(bdt)
                        .returning(DT.DT_ID).fetchOne().getDtId()
        );

        DtManifestRecord bdtManifest = new DtManifestRecord();
        bdtManifest.setDtId(bdt.getDtId());
        bdtManifest.setBasedDtManifestId(basedBdtManifest.getDtManifestId());
        bdtManifest.setReleaseId(ULong.valueOf(request.getReleaseId()));
        bdtManifest = dslContext.insertInto(DT_MANIFEST)
                .set(bdtManifest)
                .returning(DT_MANIFEST.DT_MANIFEST_ID).fetchOne();

        // insert BDT_PRI_RESTRI
        dslContext.insertInto(BDT_PRI_RESTRI,
                BDT_PRI_RESTRI.BDT_ID,
                BDT_PRI_RESTRI.CDT_AWD_PRI_XPS_TYPE_MAP_ID,
                BDT_PRI_RESTRI.CODE_LIST_ID,
                BDT_PRI_RESTRI.AGENCY_ID_LIST_ID,
                BDT_PRI_RESTRI.IS_DEFAULT)
                .select(dslContext.select(inline(bdt.getDtId()),
                        BDT_PRI_RESTRI.CDT_AWD_PRI_XPS_TYPE_MAP_ID,
                        BDT_PRI_RESTRI.CODE_LIST_ID,
                        BDT_PRI_RESTRI.AGENCY_ID_LIST_ID,
                        BDT_PRI_RESTRI.IS_DEFAULT)
                        .from(BDT_PRI_RESTRI)
                        .where(BDT_PRI_RESTRI.BDT_ID.eq(basedBdt.getDtId()))).execute();

        for(DtScRecord basedDtSc: basedBdtScList) {
            DtScRecord dtScRecord = new DtScRecord();
            DtScManifestRecord dtScManifestRecord = new DtScManifestRecord();

            dtScRecord.setGuid(ScoreGuid.randomGuid());
            dtScRecord.setPropertyTerm(basedDtSc.getPropertyTerm());
            dtScRecord.setRepresentationTerm(basedDtSc.getRepresentationTerm());
            dtScRecord.setOwnerDtId(bdt.getDtId());
            dtScRecord.setCardinalityMax(basedDtSc.getCardinalityMax());
            dtScRecord.setCardinalityMin(basedDtSc.getCardinalityMin());
            dtScRecord.setBasedDtScId(basedDtSc.getDtScId());
            dtScRecord.setDefaultValue(basedDtSc.getDefaultValue());
            dtScRecord.setFixedValue(basedDtSc.getFixedValue());
            dtScRecord.setCreatedBy(userId);
            dtScRecord.setLastUpdatedBy(userId);
            dtScRecord.setOwnerUserId(userId);
            dtScRecord.setCreationTimestamp(timestamp);
            dtScRecord.setLastUpdateTimestamp(timestamp);
            dtScRecord.setDtScId(
                    dslContext.insertInto(DT_SC)
                            .set(dtScRecord)
                            .returning(DT_SC.DT_SC_ID).fetchOne().getDtScId());

            dtScManifestRecord.setReleaseId(basedBdtManifest.getReleaseId());
            dtScManifestRecord.setDtScId(dtScRecord.getDtScId());
            dtScManifestRecord.setOwnerDtManifestId(bdtManifest.getDtManifestId());

            dtScManifestRecord.setDtScManifestId(
                    dslContext.insertInto(DT_SC_MANIFEST)
                    .set(dtScManifestRecord)
                    .returning(DT_SC_MANIFEST.DT_SC_MANIFEST_ID).fetchOne().getDtScManifestId());

            // insert BDT_SC_PRI_RESTRI
            dslContext.insertInto(BDT_SC_PRI_RESTRI,
                    BDT_SC_PRI_RESTRI.BDT_SC_ID,
                    BDT_SC_PRI_RESTRI.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID,
                    BDT_SC_PRI_RESTRI.CODE_LIST_ID,
                    BDT_SC_PRI_RESTRI.AGENCY_ID_LIST_ID,
                    BDT_SC_PRI_RESTRI.IS_DEFAULT)
                    .select(dslContext.select(inline(dtScRecord.getDtScId()),
                            BDT_SC_PRI_RESTRI.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID,
                            BDT_SC_PRI_RESTRI.CODE_LIST_ID,
                            BDT_SC_PRI_RESTRI.AGENCY_ID_LIST_ID,
                            BDT_SC_PRI_RESTRI.IS_DEFAULT)
                            .from(BDT_SC_PRI_RESTRI)
                            .where(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(basedDtSc.getDtScId()))).execute();
        }

        LogRecord logRecord =
                logRepository.insertBdtLog(
                        bdtManifest,
                        bdt,
                        LogAction.Added,
                        userId, timestamp);
        bdtManifest.setLogId(logRecord.getLogId());
        bdtManifest.update(DT_MANIFEST.LOG_ID);

        return new CreateBdtRepositoryResponse(bdtManifest.getDtManifestId().toBigInteger());
    }

    public ReviseDtRepositoryResponse reviseDt(ReviseDtRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        DtManifestRecord dtManifestRecord = dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(
                        ULong.valueOf(request.getDtManifestId())
                ))
                .fetchOne();

        DtRecord prevDtRecord = dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(dtManifestRecord.getDtId()))
                .fetchOne();

        if (user.isDeveloper()) {
            if (!CcState.Published.equals(CcState.valueOf(prevDtRecord.getState()))) {
                throw new IllegalArgumentException("Only the core component in 'Published' state can be revised.");
            }
        } else {
            if (!CcState.Production.equals(CcState.valueOf(prevDtRecord.getState()))) {
                throw new IllegalArgumentException("Only the core component in 'Production' state can be revised.");
            }
        }

        ULong workingReleaseId = dslContext.select(RELEASE.RELEASE_ID)
                .from(RELEASE)
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchOneInto(ULong.class);

        ULong targetReleaseId = dtManifestRecord.getReleaseId();
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
                .where(APP_USER.APP_USER_ID.eq(prevDtRecord.getOwnerUserId()))
                .fetchOneInto(Boolean.class);

        if (user.isDeveloper() != ownerIsDeveloper) {
            throw new IllegalArgumentException("It only allows to revise the component for users in the same roles.");
        }

        // creates new bdt for revised record.
        DtRecord nextDtRecord = prevDtRecord.copy();
        nextDtRecord.setState(CcState.WIP.name());
        nextDtRecord.setCreatedBy(userId);
        nextDtRecord.setLastUpdatedBy(userId);
        nextDtRecord.setOwnerUserId(userId);
        nextDtRecord.setCreationTimestamp(timestamp);
        nextDtRecord.setLastUpdateTimestamp(timestamp);
        nextDtRecord.setPrevDtId(prevDtRecord.getDtId());
        nextDtRecord.setDtId(
                dslContext.insertInto(DT)
                        .set(nextDtRecord)
                        .returning(DT.DT_ID).fetchOne().getDtId()
        );

        prevDtRecord.setNextDtId(nextDtRecord.getDtId());
        prevDtRecord.update(DT.NEXT_DT_ID);

        // creates new log for revised record.
        LogRecord logRecord =
                logRepository.insertBdtLog(
                        dtManifestRecord,
                        nextDtRecord, dtManifestRecord.getLogId(),
                        LogAction.Revised,
                        userId, timestamp);

        ULong responseDtManifestId;
        dtManifestRecord.setDtId(nextDtRecord.getDtId());
        dtManifestRecord.setLogId(logRecord.getLogId());
        dtManifestRecord.update(DT_MANIFEST.DT_ID, DT_MANIFEST.LOG_ID);

        responseDtManifestId = dtManifestRecord.getDtManifestId();

        List<DtScManifestRecord> dtScManifestRecords = dslContext.selectFrom(DT_SC_MANIFEST)
                .where(DT_SC_MANIFEST.OWNER_DT_MANIFEST_ID.eq(dtManifestRecord.getDtManifestId())).fetch();

        // copy bdt_pri_restri
        dslContext.insertInto(BDT_PRI_RESTRI,
                BDT_PRI_RESTRI.BDT_ID,
                BDT_PRI_RESTRI.CDT_AWD_PRI_XPS_TYPE_MAP_ID,
                BDT_PRI_RESTRI.CODE_LIST_ID,
                BDT_PRI_RESTRI.AGENCY_ID_LIST_ID,
                BDT_PRI_RESTRI.IS_DEFAULT)
                .select(dslContext.select(
                        inline(nextDtRecord.getDtId()),
                        BDT_PRI_RESTRI.CDT_AWD_PRI_XPS_TYPE_MAP_ID,
                        BDT_PRI_RESTRI.CODE_LIST_ID,
                        BDT_PRI_RESTRI.AGENCY_ID_LIST_ID,
                        BDT_PRI_RESTRI.IS_DEFAULT)
                        .from(BDT_PRI_RESTRI)
                        .where(BDT_PRI_RESTRI.BDT_ID.eq(prevDtRecord.getDtId()))).execute();

        // revise DT_SCs
        for(DtScManifestRecord dtScManifestRecord : dtScManifestRecords) {
            DtScRecord prevDtSc = dslContext.selectFrom(DT_SC)
                    .where(DT_SC.DT_SC_ID.eq(dtScManifestRecord.getDtScId())).fetchOne();

            DtScRecord nextDtSc = prevDtSc.copy();

            nextDtSc.setOwnerDtId(nextDtRecord.getDtId());
            nextDtSc.setPrevDtScId(prevDtSc.getDtScId());
            nextDtSc.setDtScId(
                    dslContext.insertInto(DT_SC)
                            .set(nextDtSc)
                            .returning(DT_SC.DT_SC_ID).fetchOne().getDtScId()
            );

            prevDtSc.setNextDtScId(nextDtSc.getDtScId());
            prevDtSc.update(DT_SC.NEXT_DT_SC_ID);

            dtScManifestRecord.setDtScId(nextDtSc.getDtScId());
            dtScManifestRecord.update(DT_SC_MANIFEST.DT_SC_ID);

            // copy bdt_pri_restri
            dslContext.insertInto(BDT_SC_PRI_RESTRI,
                    BDT_SC_PRI_RESTRI.BDT_SC_ID,
                    BDT_SC_PRI_RESTRI.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID,
                    BDT_SC_PRI_RESTRI.CODE_LIST_ID,
                    BDT_SC_PRI_RESTRI.AGENCY_ID_LIST_ID,
                    BDT_SC_PRI_RESTRI.IS_DEFAULT)
                    .select(dslContext.select(
                            inline(nextDtSc.getDtScId()),
                            BDT_SC_PRI_RESTRI.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID,
                            BDT_SC_PRI_RESTRI.CODE_LIST_ID,
                            BDT_SC_PRI_RESTRI.AGENCY_ID_LIST_ID,
                            BDT_SC_PRI_RESTRI.IS_DEFAULT)
                            .from(BDT_SC_PRI_RESTRI)
                            .where(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(prevDtSc.getDtScId()))).execute();
        }

        return new ReviseDtRepositoryResponse(responseDtManifestId.toBigInteger());
    }

    public UpdateDtPropertiesRepositoryResponse updateDtProperties(UpdateDtPropertiesRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        DtManifestRecord bdtManifestRecord = dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(
                        ULong.valueOf(request.getDtManifestId())
                ))
                .fetchOne();

        DtRecord dtRecord = dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(bdtManifestRecord.getDtId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(dtRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
        }

        if (!dtRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update bdt record.
        UpdateSetFirstStep<DtRecord> firstStep = dslContext.update(DT);
        UpdateSetMoreStep<DtRecord> moreStep = null;
        boolean qualifierChanged = false;
        if (compare(dtRecord.getQualifier(), request.getQualifier()) != 0) {
            qualifierChanged = true;
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(DT.QUALIFIER, request.getQualifier())
                    .set(DT.DEN, request.getQualifier() + "_ " + dtRecord.getRepresentationTerm() + ". Type");
        }
        if (compare(dtRecord.getContentComponentDefinition(), request.getContentComponentDefinition()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(DT.CONTENT_COMPONENT_DEFINITION, request.getContentComponentDefinition());
        }
        if (compare(dtRecord.getDefinition(), request.getDefinition()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(DT.DEFINITION, request.getDefinition());
        }
        if (compare(dtRecord.getDefinitionSource(), request.getDefinitionSource()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(DT.DEFINITION_SOURCE, request.getDefinitionSource());
        }
        if ((dtRecord.getIsDeprecated() == 1) != request.isDeprecated()) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(DT.IS_DEPRECATED, (byte) ((request.isDeprecated()) ? 1 : 0));
        }
        if (request.getNamespaceId() == null || request.getNamespaceId().longValue() <= 0L) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .setNull(DT.NAMESPACE_ID);
        } else {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(DT.NAMESPACE_ID, ULong.valueOf(request.getNamespaceId()));
        }

        if (moreStep != null) {
            moreStep.set(DT.LAST_UPDATED_BY, userId)
                    .set(DT.LAST_UPDATE_TIMESTAMP, timestamp)
                    .where(DT.DT_ID.eq(dtRecord.getDtId()))
                    .execute();

            dtRecord = dslContext.selectFrom(DT)
                    .where(DT.DT_ID.eq(bdtManifestRecord.getDtId()))
                    .fetchOne();
        }

        // creates new log for updated record.
        LogRecord logRecord =
                logRepository.insertBdtLog(
                        bdtManifestRecord,
                        dtRecord, bdtManifestRecord.getLogId(),
                        LogAction.Modified,
                        userId, timestamp);

        bdtManifestRecord.setLogId(logRecord.getLogId());
        bdtManifestRecord.update(DT_MANIFEST.LOG_ID);

        return new UpdateDtPropertiesRepositoryResponse(bdtManifestRecord.getDtManifestId().toBigInteger());
    }

    public UpdateDtStateRepositoryResponse updateDtState(UpdateDtStateRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        DtManifestRecord dtManifestRecord = dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(
                        ULong.valueOf(request.getDtManifestId())
                ))
                .fetchOne();

        DtRecord dtRecord = dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(dtManifestRecord.getDtId()))
                .fetchOne();

        CcState prevState = CcState.valueOf(dtRecord.getState());
        CcState nextState = request.getToState();

        if (prevState != request.getFromState()) {
            throw new IllegalArgumentException("Target core component is not in '" + request.getFromState() + "' state.");
        }

        if (!prevState.canMove(nextState)) {
            throw new IllegalArgumentException("The core component in '" + prevState + "' state cannot move to '" + nextState + "' state.");
        }

        // Change owner of CC when it restored.
        if (prevState == CcState.Deleted && nextState == CcState.WIP) {
            dtRecord.setOwnerUserId(userId);
        } else if (prevState != CcState.Deleted && !dtRecord.getOwnerUserId().equals(userId)
                && !prevState.canForceMove(request.getToState())) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        } else if (dtRecord.getNamespaceId() == null) {
            throw new IllegalArgumentException("'" + dtRecord.getDen() + "' dose not have NamespaceId.");
        }

        // update dt state.
        dtRecord.setState(nextState.name());
        if (!prevState.canForceMove(request.getToState())) {
            dtRecord.setLastUpdatedBy(userId);
            dtRecord.setLastUpdateTimestamp(timestamp);
        }
        dtRecord.update(DT.STATE,
                DT.LAST_UPDATED_BY, DT.LAST_UPDATE_TIMESTAMP, DT.OWNER_USER_ID);

        // creates new log for updated record.
        LogAction logAction = (CcState.Deleted == prevState && CcState.WIP == nextState)
                ? LogAction.Restored : LogAction.Modified;
        LogRecord logRecord =
                logRepository.insertBdtLog(
                        dtManifestRecord,
                        dtRecord, dtManifestRecord.getLogId(),
                        logAction,
                        userId, timestamp);

        dtManifestRecord.setLogId(logRecord.getLogId());
        dtManifestRecord.update(DT_MANIFEST.LOG_ID);

        return new UpdateDtStateRepositoryResponse(dtManifestRecord.getDtManifestId().toBigInteger());
    }
//
//    public DeleteBdtRepositoryResponse deleteBdt(DeleteBdtRepositoryRequest request) {
//        AppUser user = sessionService.getAppUser(request.getUser());
//        ULong userId = ULong.valueOf(user.getAppUserId());
//        LocalDateTime timestamp = request.getLocalDateTime();
//
//        DtManifestRecord bdtManifestRecord = dslContext.selectFrom(DT_MANIFEST)
//                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(
//                        ULong.valueOf(request.getBdtManifestId())
//                ))
//                .fetchOne();
//
//        DtRecord bdtRecord = dslContext.selectFrom(DT)
//                .where(DT.DT_ID.eq(bdtManifestRecord.getBdtId()))
//                .fetchOne();
//
//        if (!CcState.WIP.equals(CcState.valueOf(bdtRecord.getState()))) {
//            throw new IllegalArgumentException("Only the core component in 'WIP' state can be deleted.");
//        }
//
//        if (!bdtRecord.getOwnerUserId().equals(userId)) {
//            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
//        }
//
//        // update bdt state.
//        bdtRecord.setState(CcState.Deleted.name());
//        bdtRecord.setLastUpdatedBy(userId);
//        bdtRecord.setLastUpdateTimestamp(timestamp);
//        bdtRecord.update(DT.STATE,
//                DT.LAST_UPDATED_BY, DT.LAST_UPDATE_TIMESTAMP);
//
//        // creates new log for deleted record.
//        LogRecord logRecord =
//                logRepository.insertBdtLog(
//                        bdtManifestRecord,
//                        bdtRecord, bdtManifestRecord.getLogId(),
//                        LogAction.Deleted,
//                        userId, timestamp);
//
//        bdtManifestRecord.setLogId(logRecord.getLogId());
//        bdtManifestRecord.update(DT_MANIFEST.LOG_ID);
//
//        return new DeleteBdtRepositoryResponse(bdtManifestRecord.getBdtManifestId().toBigInteger());
//    }
//
//    public UpdateBdtOwnerRepositoryResponse updateBdtOwner(UpdateBdtOwnerRepositoryRequest request) {
//        AppUser user = sessionService.getAppUser(request.getUser());
//        ULong userId = ULong.valueOf(user.getAppUserId());
//        LocalDateTime timestamp = request.getLocalDateTime();
//
//        DtManifestRecord bdtManifestRecord = dslContext.selectFrom(DT_MANIFEST)
//                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(
//                        ULong.valueOf(request.getBdtManifestId())
//                ))
//                .fetchOne();
//
//        DtRecord bdtRecord = dslContext.selectFrom(DT)
//                .where(DT.DT_ID.eq(bdtManifestRecord.getBdtId()))
//                .fetchOne();
//
//        if (!CcState.WIP.equals(CcState.valueOf(bdtRecord.getState()))) {
//            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
//        }
//
//        if (!bdtRecord.getOwnerUserId().equals(userId)) {
//            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
//        }
//
//        bdtRecord.setOwnerUserId(ULong.valueOf(request.getOwnerId()));
//        bdtRecord.setLastUpdatedBy(userId);
//        bdtRecord.setLastUpdateTimestamp(timestamp);
//        bdtRecord.update(DT.OWNER_USER_ID, DT.LAST_UPDATED_BY, DT.LAST_UPDATE_TIMESTAMP);
//
//        LogRecord logRecord =
//                logRepository.insertBdtLog(
//                        bdtManifestRecord,
//                        bdtRecord, bdtManifestRecord.getLogId(),
//                        LogAction.Modified,
//                        userId, timestamp);
//
//        bdtManifestRecord.setLogId(logRecord.getLogId());
//        bdtManifestRecord.update(DT_MANIFEST.LOG_ID);
//
//        return new UpdateBdtOwnerRepositoryResponse(bdtManifestRecord.getBdtManifestId().toBigInteger());
//    }
//
    public CancelRevisionDtRepositoryResponse cancelRevisionDt(CancelRevisionDtRepositoryRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(request.getUser()));
        LocalDateTime timestamp = request.getLocalDateTime();

        DtManifestRecord dtManifestRecord = dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(request.getDtManifestId()))).fetchOne();

        List<DtScManifestRecord> dtScManifestRecords = dslContext.selectFrom(DT_SC_MANIFEST)
                .where(DT_SC_MANIFEST.OWNER_DT_MANIFEST_ID.eq(dtManifestRecord.getDtManifestId())).fetch();

        if (dtManifestRecord == null) {
            throw new IllegalArgumentException("Not found a target DT");
        }

        DtRecord bdtRecord = dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(dtManifestRecord.getDtId())).fetchOne();

        if (bdtRecord.getPrevDtId() == null) {
            throw new IllegalArgumentException("Not found previous log");
        }

        DtRecord prevDtRecord = dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(bdtRecord.getPrevDtId())).fetchOne();

        dslContext.deleteFrom(BDT_PRI_RESTRI)
                .where(BDT_PRI_RESTRI.BDT_ID.eq(bdtRecord.getDtId())).execute();

        // unlink prev DT
        prevDtRecord.setNextDtId(null);
        prevDtRecord.update(DT.NEXT_DT_ID);

        // remove revised DT_SCs
        for(DtScManifestRecord dtScManifestRecord : dtScManifestRecords) {
            dslContext.deleteFrom(BDT_SC_PRI_RESTRI)
                    .where(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtScManifestRecord.getDtScId())).execute();

            DtScRecord currentDtSc = dslContext.selectFrom(DT_SC)
                    .where(DT_SC.DT_SC_ID.eq(dtScManifestRecord.getDtScId())).fetchOne();

            DtScRecord prevDtSc = dslContext.selectFrom(DT_SC)
                    .where(DT_SC.DT_SC_ID.eq(currentDtSc.getPrevDtScId())).fetchOne();

            prevDtSc.setNextDtScId(null);
            prevDtSc.update(DT_SC.NEXT_DT_SC_ID);

            dtScManifestRecord.setDtScId(prevDtSc.getDtScId());
            dtScManifestRecord.update(DT_SC_MANIFEST.DT_SC_ID);

            currentDtSc.delete();
        }

        // clean logs up
        logRepository.revertToStableState(dtManifestRecord);

        dtManifestRecord.setDtId(prevDtRecord.getDtId());
        dtManifestRecord.update(DT_MANIFEST.DT_ID);

        // delete current DT
        bdtRecord.delete();

        return new CancelRevisionDtRepositoryResponse(request.getDtManifestId());
    }

//    public CancelRevisionDtRepositoryResponse resetLogDt(CancelRevisionDtRepositoryRequest request) {
//        DtManifestRecord bdtManifestRecord = dslContext.selectFrom(DT_MANIFEST)
//                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(request.getDtManifestId()))).fetchOne();
//
//        if (bdtManifestRecord == null) {
//            throw new IllegalArgumentException("Not found a target DT");
//        }
//
//        DtRecord bdtRecord = dslContext.selectFrom(DT)
//                .where(DT.DT_ID.eq(bdtManifestRecord.getDtId())).fetchOne();
//
//        LogRecord cursorLog = dslContext.selectFrom(LOG)
//                .where(LOG.LOG_ID.eq(bdtManifestRecord.getLogId())).fetchOne();
//
//        UInteger logNum = cursorLog.getRevisionNum();
//
//        if (cursorLog.getPrevLogId() == null) {
//            throw new IllegalArgumentException("There is no change to be reset.");
//        }
//
//        List<ULong> deleteLogTargets = new ArrayList<>();
//
//        while (cursorLog.getPrevLogId() != null) {
//            if (!cursorLog.getRevisionNum().equals(logNum)) {
//                throw new IllegalArgumentException("Cannot find reset point");
//            }
//            if (cursorLog.getRevisionTrackingNum().equals(UInteger.valueOf(1))) {
//                break;
//            }
//            deleteLogTargets.add(cursorLog.getLogId());
//            cursorLog = dslContext.selectFrom(LOG)
//                    .where(LOG.LOG_ID.eq(cursorLog.getPrevLogId())).fetchOne();
//        }
//
//        JsonObject snapshot = serializer.deserialize(cursorLog.getSnapshot().toString());
//
//        ULong bdtId = serializer.getSnapshotId(snapshot.get("bdtId"));
//        DtManifestRecord bdtManifestRecord = dslContext.selectFrom(DT_MANIFEST).where(and(
//                DT_MANIFEST.DT_ID.eq(bdtId),
//                DT_MANIFEST.RELEASE_ID.eq(bdtManifestRecord.getReleaseId())
//        )).fetchOne();
//
//        if (bdtManifestRecord == null) {
//            throw new IllegalArgumentException("Not found based BDT.");
//        }
//
//        bdtManifestRecord.setDtManifestId(bdtManifestRecord.getDtManifestId());
//        bdtManifestRecord.setLogId(cursorLog.getLogId());
//        bdtManifestRecord.update(DT_MANIFEST.BDT_MANIFEST_ID, DT_MANIFEST.LOG_ID);
//
//        bdtRecord.setDtId(bdtManifestRecord.getDtId());
//        bdtRecord.setPropertyTerm(serializer.getSnapshotString(snapshot.get("propertyTerm")));
//        bdtRecord.setRepresentationTerm(serializer.getSnapshotString(snapshot.get("representationTerm")));
//        bdtRecord.setDen(bdtRecord.getPropertyTerm() + ". " + bdtRecord.getRepresentationTerm());
//        bdtRecord.setDefinition(serializer.getSnapshotString(snapshot.get("definition")));
//        bdtRecord.setDefinitionSource(serializer.getSnapshotString(snapshot.get("definitionSource")));
//        bdtRecord.setNamespaceId(serializer.getSnapshotId(snapshot.get("namespaceId")));
//        bdtRecord.setIsDeprecated(serializer.getSnapshotByte(snapshot.get("deprecated")));
//        bdtRecord.setIsNillable(serializer.getSnapshotByte(snapshot.get("nillable")));
//        bdtRecord.setDefaultValue(serializer.getSnapshotString(snapshot.get("defaultValue")));
//        bdtRecord.setFixedValue(serializer.getSnapshotString(snapshot.get("fixedValue")));
//        bdtRecord.update();
//
//        cursorLog.setNextLogId(null);
//        cursorLog.update(LOG.NEXT_LOG_ID);
//        dslContext.update(LOG)
//                .setNull(LOG.PREV_LOG_ID)
//                .setNull(LOG.NEXT_LOG_ID)
//                .where(LOG.LOG_ID.in(deleteLogTargets))
//                .execute();
//        dslContext.deleteFrom(LOG).where(LOG.LOG_ID.in(deleteLogTargets)).execute();
//
//        return new CancelRevisionDtRepositoryResponse(request.getDtManifestId());
//    }

    public CreateDtScRepositoryResponse createDtSc(CreateDtScRepositoryRequest request) {

        DtManifestRecord targetDtManifest = dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(request.getTargetDdtManifestId()))).fetchOne();

        if(targetDtManifest == null) {
            throw new IllegalArgumentException("Can not found target DT manifest.");
        }

        DtRecord targetDtRecord = dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(targetDtManifest.getDtId())).fetchOne();

        if(targetDtRecord == null) {
            throw new IllegalArgumentException("Can not found target DT.");
        }

        DtManifestRecord ownerDtManifest = dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(request.getOwnerDdtManifestId()))).fetchOne();

        DtScRecord dtScRecord = new DtScRecord();
        dtScRecord.setGuid(ScoreGuid.randomGuid());
        dtScRecord.setPropertyTerm("Property Term");
        dtScRecord.setRepresentationTerm(targetDtRecord.getDataTypeTerm());
        dtScRecord.setOwnerDtId(ownerDtManifest.getDtId());
        dtScRecord.setCardinalityMin(0);
        dtScRecord.setCardinalityMax(1);

        dtScRecord.setDtScId(
                dslContext.insertInto(DT_SC)
                        .set(dtScRecord)
                        .returning(DT_SC.DT_SC_ID).fetchOne().getDtScId()
        );

        DtScManifestRecord dtScManifestRecord = new DtScManifestRecord();
        dtScManifestRecord.setDtScId(dtScRecord.getDtScId());
        dtScManifestRecord.setReleaseId(ownerDtManifest.getReleaseId());
        dtScManifestRecord.setOwnerDtManifestId(ownerDtManifest.getDtManifestId());

        dtScManifestRecord.setDtScManifestId(
                dslContext.insertInto(DT_SC_MANIFEST)
                        .set(dtScManifestRecord)
                        .returning(DT_SC_MANIFEST.DT_SC_MANIFEST_ID).fetchOne().getDtScManifestId()
        );

        return new CreateDtScRepositoryResponse(dtScManifestRecord.getDtScManifestId().toBigInteger());
    }

    public void addDtPrimitiveRestriction(CreatePrimitiveRestrictionRepositoryRequest request) {

        DtManifestRecord dtManifestRecord = dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(request.getDtManifestId())))
                .fetchOne();

        List<BdtPriRestriRecord> bdtPriRestriRecords = dslContext.selectFrom(BDT_PRI_RESTRI)
                .where(BDT_PRI_RESTRI.BDT_ID.eq(dtManifestRecord.getDtId()))
                .fetch();

        CdtPriRecord cdtPriRecord = dslContext.selectFrom(CDT_PRI)
                .where(CDT_PRI.NAME.eq(request.getPrimitive()))
                .fetchOne();

        CdtAwdPriRecord cdtAwdPriRecord = new CdtAwdPriRecord();
        cdtAwdPriRecord.setCdtId(dtManifestRecord.getDtId());
        cdtAwdPriRecord.setCdtPriId(cdtPriRecord.getCdtPriId());
        cdtAwdPriRecord.setIsDefault((byte) 0);

        cdtAwdPriRecord.setCdtAwdPriId(dslContext.insertInto(CDT_AWD_PRI)
                .set(cdtAwdPriRecord).returning(CDT_AWD_PRI.CDT_AWD_PRI_ID)
                .fetchOne().getCdtAwdPriId());

        boolean isDefault = bdtPriRestriRecords.size() == 0;

        for (BigInteger xbtManifestId: request.getXbtManifestIdList()) {
            XbtManifestRecord xbtManifestRecord = dslContext.selectFrom(XBT_MANIFEST)
                    .where(XBT_MANIFEST.XBT_MANIFEST_ID.eq(ULong.valueOf(xbtManifestId)))
                    .fetchOne();

            String duplicated = dslContext.select(CDT_PRI.NAME).from(BDT_PRI_RESTRI)
                    .join(CDT_AWD_PRI_XPS_TYPE_MAP).on(BDT_PRI_RESTRI.CDT_AWD_PRI_XPS_TYPE_MAP_ID.eq(CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_XPS_TYPE_MAP_ID))
                    .join(XBT).on(CDT_AWD_PRI_XPS_TYPE_MAP.XBT_ID.eq(XBT.XBT_ID))
                    .join(CDT_AWD_PRI).on(CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_ID.eq(CDT_AWD_PRI.CDT_AWD_PRI_ID))
                    .join(CDT_PRI).on(CDT_AWD_PRI.CDT_PRI_ID.eq(CDT_PRI.CDT_PRI_ID))
                    .where(and(BDT_PRI_RESTRI.BDT_ID.eq(dtManifestRecord.getDtId()),
                            CDT_PRI.NAME.eq(request.getPrimitive()),
                            XBT.XBT_ID.eq(xbtManifestRecord.getXbtId())))
                    .fetchOneInto(String.class);

            if (duplicated != null) {
                throw new IllegalArgumentException("Duplicated Primitive already exist.");
            }

            CdtAwdPriXpsTypeMapRecord cdtAwdPriXpsTypeMapRecord = new CdtAwdPriXpsTypeMapRecord();
            cdtAwdPriXpsTypeMapRecord.setCdtAwdPriId(cdtAwdPriRecord.getCdtAwdPriId());
            cdtAwdPriXpsTypeMapRecord.setXbtId(xbtManifestRecord.getXbtId());

            cdtAwdPriXpsTypeMapRecord.setCdtAwdPriXpsTypeMapId(dslContext.insertInto(CDT_AWD_PRI_XPS_TYPE_MAP)
                    .set(cdtAwdPriXpsTypeMapRecord).returning(CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_XPS_TYPE_MAP_ID)
                    .fetchOne().getCdtAwdPriXpsTypeMapId());

            BdtPriRestriRecord bdtPriRestriRecord = new BdtPriRestriRecord();
            bdtPriRestriRecord.setCdtAwdPriXpsTypeMapId(cdtAwdPriXpsTypeMapRecord.getCdtAwdPriXpsTypeMapId());
            bdtPriRestriRecord.setBdtId(dtManifestRecord.getDtId());

            if(isDefault) {
                bdtPriRestriRecord.setIsDefault((byte) 1);
                isDefault = false;
            } else {
                bdtPriRestriRecord.setIsDefault((byte) 0);
            }

            dslContext.insertInto(BDT_PRI_RESTRI).set(bdtPriRestriRecord).execute();
        }
    }

    public void addDtCodeListRestriction(CreateCodeListRestrictionRepositoryRequest request) {
        CodeListManifestRecord codeListManifestRecord = dslContext.selectFrom(CODE_LIST_MANIFEST)
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(ULong.valueOf(request.getCodeListManifestId())))
                .fetchOne();

        CodeListRecord codeListRecord = dslContext.selectFrom(CODE_LIST)
                .where(CODE_LIST.CODE_LIST_ID.eq(codeListManifestRecord.getCodeListId()))
                .fetchOne();

        DtManifestRecord dtManifestRecord = dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(request.getDtManifestId())))
                .fetchOne();

        List<BdtPriRestriRecord> bdtPriRestriRecords = dslContext.selectFrom(BDT_PRI_RESTRI)
                .where(BDT_PRI_RESTRI.BDT_ID.eq(dtManifestRecord.getDtId()))
                .fetch();

        BdtPriRestriRecord bdtPriRestriRecord = new BdtPriRestriRecord();

        if (bdtPriRestriRecords.size() == 0) {
            bdtPriRestriRecord.setIsDefault((byte) 1);
        } else {
            bdtPriRestriRecord.setIsDefault((byte) 0);
        }

        if (bdtPriRestriRecords.stream().anyMatch(e -> codeListManifestRecord.getCodeListId().equals(e.getCodeListId()))) {
            throw new IllegalArgumentException("Duplicated Code List already exist.");
        }

        bdtPriRestriRecord.setCodeListId(codeListManifestRecord.getCodeListId());
        bdtPriRestriRecord.setBdtId(dtManifestRecord.getDtId());
        
        dslContext.insertInto(BDT_PRI_RESTRI).set(bdtPriRestriRecord).execute();
    }

    public void addDtAgencyIdListRestriction(CreateAgencyIdListRestrictionRepositoryRequest request) {
        AgencyIdListManifestRecord agencyIdListManifestRecord = dslContext.selectFrom(AGENCY_ID_LIST_MANIFEST)
                .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(ULong.valueOf(request.getAgencyIdListManifestId())))
                .fetchOne();

        AgencyIdListRecord agencyIdListRecord = dslContext.selectFrom(AGENCY_ID_LIST)
                .where(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(agencyIdListManifestRecord.getAgencyIdListId()))
                .fetchOne();

        DtManifestRecord dtManifestRecord = dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(request.getDtManifestId())))
                .fetchOne();

        List<BdtPriRestriRecord> bdtPriRestriRecords = dslContext.selectFrom(BDT_PRI_RESTRI)
                .where(BDT_PRI_RESTRI.BDT_ID.eq(dtManifestRecord.getDtId()))
                .fetch();

        BdtPriRestriRecord bdtPriRestriRecord = new BdtPriRestriRecord();

        if (bdtPriRestriRecords.size() == 0) {
            bdtPriRestriRecord.setIsDefault((byte) 1);
        } else {
            bdtPriRestriRecord.setIsDefault((byte) 0);
        }

        if (bdtPriRestriRecords.stream().anyMatch(e -> agencyIdListManifestRecord.getAgencyIdListId().equals(e.getAgencyIdListId()))) {
            throw new IllegalArgumentException("Duplicated Agency Id List already exist.");
        }

        bdtPriRestriRecord.setAgencyIdListId(agencyIdListManifestRecord.getAgencyIdListId());
        bdtPriRestriRecord.setBdtId(dtManifestRecord.getDtId());

        dslContext.insertInto(BDT_PRI_RESTRI).set(bdtPriRestriRecord).execute();
    }
}