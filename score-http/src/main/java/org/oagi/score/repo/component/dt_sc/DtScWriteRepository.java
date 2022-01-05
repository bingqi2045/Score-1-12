package org.oagi.score.repo.component.dt_sc;

import org.jooq.DSLContext;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.types.ULong;
import org.oagi.score.gateway.http.api.cc_management.data.node.CcBdtScPriResri;
import org.oagi.score.gateway.http.api.cc_management.data.node.CcXbt;
import org.oagi.score.gateway.http.api.cc_management.data.node.PrimitiveRestriType;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.repo.api.impl.jooq.entity.tables.DtSc;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.repo.component.dt.*;
import org.oagi.score.service.common.data.AppUser;
import org.oagi.score.service.common.data.CcState;
import org.oagi.score.service.log.LogRepository;
import org.oagi.score.service.log.model.LogAction;
import org.oagi.score.service.log.model.LogSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.compare;
import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Repository
public class DtScWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private LogSerializer serializer;

    private void updateDerivedSc(DtScManifestRecord baseDtScManifestRecord, DtScRecord baseDtScRecord) {

        dslContext.selectFrom(DT_SC_MANIFEST).where(DT_SC_MANIFEST.BASED_DT_SC_MANIFEST_ID.eq(baseDtScManifestRecord.getDtScManifestId()))
                .fetchStream().forEach(dtScManifestRecord -> {
                    DtScRecord dtScRecord = dslContext.selectFrom(DT_SC)
                            .where(DT_SC.DT_SC_ID.eq(dtScManifestRecord.getDtScId())).fetchOne();

            // update bdtSc record.
            UpdateSetFirstStep<DtScRecord> firstStep = dslContext.update(DT_SC);
            UpdateSetMoreStep<DtScRecord> moreStep = null;
            if (compare(dtScRecord.getPropertyTerm(), baseDtScRecord.getPropertyTerm()) != 0) {
                moreStep = ((moreStep != null) ? moreStep : firstStep)
                        .set(DT_SC.PROPERTY_TERM, baseDtScRecord.getPropertyTerm());
            }
            if (compare(dtScRecord.getRepresentationTerm(), baseDtScRecord.getRepresentationTerm()) != 0) {
                moreStep = ((moreStep != null) ? moreStep : firstStep)
                        .set(DT_SC.REPRESENTATION_TERM, baseDtScRecord.getRepresentationTerm());
            }
            if (!dtScRecord.getCardinalityMax().equals(baseDtScRecord.getCardinalityMax())) {
                moreStep = ((moreStep != null) ? moreStep : firstStep)
                        .set(DT_SC.CARDINALITY_MAX, baseDtScRecord.getCardinalityMax());
            }
            if (!dtScRecord.getCardinalityMin().equals(baseDtScRecord.getCardinalityMin())) {
                moreStep = ((moreStep != null) ? moreStep : firstStep)
                        .set(DT_SC.CARDINALITY_MIN, baseDtScRecord.getCardinalityMin());
            }
            if (compare(dtScRecord.getDefinition(), baseDtScRecord.getDefinition()) != 0) {
                moreStep = ((moreStep != null) ? moreStep : firstStep)
                        .set(DT_SC.DEFINITION, baseDtScRecord.getDefinition());
            }
            if (compare(dtScRecord.getDefinitionSource(), baseDtScRecord.getDefinitionSource()) != 0) {
                moreStep = ((moreStep != null) ? moreStep : firstStep)
                        .set(DT_SC.DEFINITION_SOURCE, baseDtScRecord.getDefinitionSource());
            }
            if (compare(dtScRecord.getDefaultValue(), baseDtScRecord.getDefaultValue()) != 0) {
                moreStep = ((moreStep != null) ? moreStep : firstStep)
                        .set(DT_SC.DEFAULT_VALUE, baseDtScRecord.getDefaultValue())
                        .setNull(DT_SC.FIXED_VALUE);
            } else if (compare(dtScRecord.getFixedValue(), baseDtScRecord.getFixedValue()) != 0) {
                moreStep = ((moreStep != null) ? moreStep : firstStep)
                        .set(DT_SC.FIXED_VALUE, baseDtScRecord.getFixedValue())
                        .setNull(DT_SC.DEFAULT_VALUE);
            }

            if (moreStep != null) {
                moreStep.set(DT_SC.LAST_UPDATED_BY, baseDtScRecord.getLastUpdatedBy())
                        .set(DT_SC.LAST_UPDATE_TIMESTAMP, baseDtScRecord.getLastUpdateTimestamp())
                        .where(DT_SC.DT_SC_ID.eq(dtScRecord.getDtScId()))
                        .execute();

                updateDerivedSc(dtScManifestRecord, dslContext.selectFrom(DT_SC)
                        .where(DT_SC.DT_SC_ID.eq(dtScRecord.getDtScId())).fetchOne());
            }
        });
    }

    public UpdateDtScPropertiesRepositoryResponse updateDtScProperties(UpdateDtScPropertiesRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        DtScManifestRecord dtScManifestRecord = dslContext.selectFrom(DT_SC_MANIFEST)
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(
                        ULong.valueOf(request.getDtScManifestId())
                ))
                .fetchOne();

        DtScRecord dtScRecord = dslContext.selectFrom(DT_SC)
                .where(DT_SC.DT_SC_ID.eq(dtScManifestRecord.getDtScId()))
                .fetchOne();

        DtManifestRecord dtManifestRecord = dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(dtScManifestRecord.getOwnerDtManifestId()))
                .fetchOne();

        DtRecord dtRecord = dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(dtManifestRecord.getDtId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(dtRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
        }

        if (!dtRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        if (dslContext.selectFrom(DT_SC).where(
                and(DT_SC.PROPERTY_TERM.eq(request.getPropertyTerm()),
                    DT_SC.REPRESENTATION_TERM.eq(request.getRepresentationTerm()),
                    DT_SC.OBJECT_CLASS_TERM.eq(dtScRecord.getObjectClassTerm()),
                    DT_SC.DT_SC_ID.notEqual(dtScRecord.getDtScId()),
                    DT_SC.OWNER_DT_ID.eq(dtScRecord.getOwnerDtId())))
        .fetch().size() > 0) {
            throw new IllegalArgumentException("Duplicated supplementary components already exist.");
        }

        // update bdtSc record.
        UpdateSetFirstStep<DtScRecord> firstStep = dslContext.update(DT_SC);
        UpdateSetMoreStep<DtScRecord> moreStep = null;
        if (compare(dtScRecord.getPropertyTerm(), request.getPropertyTerm()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(DT_SC.PROPERTY_TERM, request.getPropertyTerm());
        }
        if (compare(dtScRecord.getRepresentationTerm(), request.getRepresentationTerm()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(DT_SC.REPRESENTATION_TERM, request.getRepresentationTerm());
        }
        if (!dtScRecord.getCardinalityMax().equals(request.getCardinalityMax())) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(DT_SC.CARDINALITY_MAX, request.getCardinalityMax());
        }
        if (!dtScRecord.getCardinalityMin().equals(request.getCardinalityMin())) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(DT_SC.CARDINALITY_MIN, request.getCardinalityMin());
        }
        if (compare(dtScRecord.getDefinition(), request.getDefinition()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(DT_SC.DEFINITION, request.getDefinition());
        }
        if (compare(dtScRecord.getDefinitionSource(), request.getDefinitionSource()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(DT_SC.DEFINITION_SOURCE, request.getDefinitionSource());
        }
        if (compare(dtScRecord.getDefaultValue(), request.getDefaultValue()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(DT_SC.DEFAULT_VALUE, request.getDefaultValue())
                    .setNull(DT_SC.FIXED_VALUE);
        } else if (compare(dtScRecord.getFixedValue(), request.getFixedValue()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(DT_SC.FIXED_VALUE, request.getFixedValue())
                    .setNull(DT_SC.DEFAULT_VALUE);
        }

        if (moreStep != null) {
            moreStep.set(DT_SC.LAST_UPDATED_BY, userId)
                    .set(DT_SC.LAST_UPDATE_TIMESTAMP, timestamp)
                    .where(DT_SC.DT_SC_ID.eq(dtScRecord.getDtScId()))
                    .execute();

            updateDerivedSc(dtScManifestRecord, dslContext.selectFrom(DT_SC)
                    .where(DT_SC.DT_SC_ID.eq(dtScRecord.getDtScId())).fetchOne());
        }

        updateBdtScPriList(dtScRecord.getDtScId(), request.getCcBdtScPriResriList());

        // creates new log for updated record.
        LogRecord logRecord =
                logRepository.insertBdtLog(
                        dtManifestRecord,
                        dtRecord, dtManifestRecord.getLogId(),
                        LogAction.Modified,
                        userId, timestamp);

        return new UpdateDtScPropertiesRepositoryResponse(dtScManifestRecord.getDtScManifestId().toBigInteger());
    }

    private void deleteDerivedValueDomain(ULong dtScId, List<BdtScPriRestriRecord> deleteList) {
        List<DtScRecord> derivedDtScList = dslContext.selectFrom(DT_SC).where(DT_SC.BASED_DT_SC_ID.eq(dtScId)).fetch();

        List<ULong> cdtScAwdPriXpsTypeMapIdList = deleteList.stream().filter(e -> e.getCdtScAwdPriXpsTypeMapId() != null)
                .map(BdtScPriRestriRecord::getCdtScAwdPriXpsTypeMapId).collect(Collectors.toList());

        List<ULong> codeListId = deleteList.stream().filter(e -> e.getCodeListId() != null)
                .map(BdtScPriRestriRecord::getCodeListId).collect(Collectors.toList());

        List<ULong> agencyIdList = deleteList.stream().filter(e -> e.getAgencyIdListId() != null)
                .map(BdtScPriRestriRecord::getAgencyIdListId).collect(Collectors.toList());

        for (DtScRecord dtSc: derivedDtScList) {
            deleteDerivedValueDomain(dtSc.getDtScId(), deleteList);
            dslContext.deleteFrom(BDT_SC_PRI_RESTRI).where(
                    and(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtSc.getDtScId())),
                    BDT_SC_PRI_RESTRI.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID.in(cdtScAwdPriXpsTypeMapIdList))
                    .execute();
            dslContext.deleteFrom(BDT_SC_PRI_RESTRI).where(
                    and(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtSc.getDtScId())),
                    BDT_SC_PRI_RESTRI.CODE_LIST_ID.in(codeListId))
                    .execute();
            dslContext.deleteFrom(BDT_SC_PRI_RESTRI).where(
                    and(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtSc.getDtScId())),
                    BDT_SC_PRI_RESTRI.AGENCY_ID_LIST_ID.in(agencyIdList))
                    .execute();
            BdtScPriRestriRecord defaultRecord = dslContext.selectFrom(BDT_SC_PRI_RESTRI).where(
                    and(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtSc.getDtScId())),
                    BDT_SC_PRI_RESTRI.IS_DEFAULT.eq((byte) 1)).fetchOne();
            if (defaultRecord == null) {
                BdtScPriRestriRecord baseDefaultRecord = dslContext.selectFrom(BDT_SC_PRI_RESTRI).where(and(
                        BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtScId),
                        BDT_SC_PRI_RESTRI.IS_DEFAULT.eq((byte) 1))).fetchOne();

                if (baseDefaultRecord.getCdtScAwdPriXpsTypeMapId() != null) {
                    dslContext.update(BDT_SC_PRI_RESTRI).set(BDT_SC_PRI_RESTRI.IS_DEFAULT, (byte) 1)
                            .where(and(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtSc.getDtScId()),
                                    BDT_SC_PRI_RESTRI.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID.eq(baseDefaultRecord.getCdtScAwdPriXpsTypeMapId())))
                            .execute();
                } else if (baseDefaultRecord.getCodeListId() != null) {
                    dslContext.update(BDT_SC_PRI_RESTRI).set(BDT_SC_PRI_RESTRI.IS_DEFAULT, (byte) 1)
                            .where(and(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtSc.getDtScId()),
                                    BDT_SC_PRI_RESTRI.CODE_LIST_ID.eq(baseDefaultRecord.getCodeListId())))
                            .execute();
                } else {
                    dslContext.update(BDT_SC_PRI_RESTRI).set(BDT_SC_PRI_RESTRI.IS_DEFAULT, (byte) 1)
                            .where(and(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtSc.getDtScId()),
                                    BDT_SC_PRI_RESTRI.AGENCY_ID_LIST_ID.eq(baseDefaultRecord.getAgencyIdListId())))
                            .execute();
                }
            }
        }
    }

    private void insertDerivedValueDomain(ULong dtScId, List<BdtScPriRestriRecord> insertList) {
        List<DtScRecord> derivedDtScList = dslContext.selectFrom(DT_SC).where(DT_SC.BASED_DT_SC_ID.eq(dtScId)).fetch();

        List<ULong> cdtScAwdPriXpsTypeMapIdList = insertList.stream().filter(e -> e.getCdtScAwdPriXpsTypeMapId() != null)
                .map(BdtScPriRestriRecord::getCdtScAwdPriXpsTypeMapId).collect(Collectors.toList());

        List<ULong> codeListIdList = insertList.stream().filter(e -> e.getCodeListId() != null)
                .map(BdtScPriRestriRecord::getCodeListId).collect(Collectors.toList());

        List<ULong> agencyIdList = insertList.stream().filter(e -> e.getAgencyIdListId() != null)
                .map(BdtScPriRestriRecord::getAgencyIdListId).collect(Collectors.toList());

        for (DtScRecord dtSc: derivedDtScList) {
            for (ULong cdtScAwdPriXpsTypeMapId: cdtScAwdPriXpsTypeMapIdList) {
                BdtScPriRestriRecord existRecord = dslContext.selectFrom(BDT_SC_PRI_RESTRI).where(
                        and(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtSc.getDtScId())),
                        BDT_SC_PRI_RESTRI.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID.eq(cdtScAwdPriXpsTypeMapId)).fetchOne();
                if (existRecord == null) {
                    dslContext.insertInto(BDT_SC_PRI_RESTRI)
                            .set(BDT_SC_PRI_RESTRI.BDT_SC_ID, dtSc.getDtScId())
                            .set(BDT_SC_PRI_RESTRI.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID, cdtScAwdPriXpsTypeMapId)
                            .set(BDT_SC_PRI_RESTRI.IS_DEFAULT, (byte) 0).execute();
                }
            }

            for (ULong codeListId: codeListIdList) {
                BdtScPriRestriRecord existRecord = dslContext.selectFrom(BDT_SC_PRI_RESTRI).where(
                        and(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtSc.getDtScId())),
                        BDT_SC_PRI_RESTRI.CODE_LIST_ID.eq(codeListId)).fetchOne();
                if (existRecord == null) {
                    dslContext.insertInto(BDT_SC_PRI_RESTRI)
                            .set(BDT_SC_PRI_RESTRI.BDT_SC_ID, dtSc.getDtScId())
                            .set(BDT_SC_PRI_RESTRI.CODE_LIST_ID, codeListId)
                            .set(BDT_SC_PRI_RESTRI.IS_DEFAULT, (byte) 0).execute();
                }
            }

            for (ULong agencyId: agencyIdList) {
                BdtScPriRestriRecord existRecord = dslContext.selectFrom(BDT_SC_PRI_RESTRI).where(
                        and(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtSc.getDtScId())),
                        BDT_SC_PRI_RESTRI.AGENCY_ID_LIST_ID.eq(agencyId)).fetchOne();
                if (existRecord == null) {
                    dslContext.insertInto(BDT_SC_PRI_RESTRI)
                            .set(BDT_SC_PRI_RESTRI.BDT_SC_ID, dtSc.getDtScId())
                            .set(BDT_SC_PRI_RESTRI.AGENCY_ID_LIST_ID, agencyId)
                            .set(BDT_SC_PRI_RESTRI.IS_DEFAULT, (byte) 0).execute();
                }
            }

            insertDerivedValueDomain(dtSc.getDtScId(), insertList);
        }
    }

    private void updateBdtScPriList(ULong dtScId, List<CcBdtScPriResri> list) {
        List<BdtScPriRestriRecord> records = dslContext
                .selectFrom(BDT_SC_PRI_RESTRI)
                .where(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtScId)).fetch();

        List<BdtScPriRestriRecord> deleteList = new ArrayList<>();

        records.forEach(r -> {
            if (!list.stream().map(CcBdtScPriResri::getBdtScPriRestriId).collect(Collectors.toList())
                    .contains(r.getBdtScPriRestriId().toBigInteger())) {
                deleteList.add(r);
            }
        });

        if (deleteList.size() > 0) {
            deleteDerivedValueDomain(dtScId, deleteList);
        }

        dslContext.deleteFrom(BDT_SC_PRI_RESTRI).where(BDT_SC_PRI_RESTRI.BDT_SC_PRI_RESTRI_ID.in(
                deleteList.stream().map(BdtScPriRestriRecord::getBdtScPriRestriId).collect(Collectors.toList())))
                .execute();

        BigInteger defaultValueDomainId = null;

        List<BdtScPriRestriRecord> insertedList = new ArrayList<>();

        for (CcBdtScPriResri restri: list) {
            if(restri.getBdtScPriRestriId() == null) {
                // insert
                BdtScPriRestriRecord newBdtScPriRestri = new BdtScPriRestriRecord();
                newBdtScPriRestri.setIsDefault((byte) 0);
                newBdtScPriRestri.setBdtScId(dtScId);
                if(restri.getType().equals(PrimitiveRestriType.CodeList)) {
                    newBdtScPriRestri.setCodeListId(ULong.valueOf(restri.getCodeListId()));
                } else if(restri.getType().equals(PrimitiveRestriType.AgencyIdList)) {
                    newBdtScPriRestri.setAgencyIdListId(ULong.valueOf(restri.getAgencyIdListId()));
                } else {
                    newBdtScPriRestri.setCdtScAwdPriXpsTypeMapId(
                            ULong.valueOf(restri.getCdtScAwdPriXpsTypeMapId()));
                }
                restri.setBdtScPriRestriId(dslContext.insertInto(BDT_SC_PRI_RESTRI)
                        .set(newBdtScPriRestri)
                        .returning(BDT_SC_PRI_RESTRI.BDT_SC_PRI_RESTRI_ID).fetchOne().getBdtScPriRestriId().toBigInteger());

                insertedList.add(newBdtScPriRestri);
            }

            if (restri.isDefault()) {
                if (restri.getType().equals(PrimitiveRestriType.Primitive)) {
                    CcXbt defaultXbt = restri.getXbtList().stream().filter(CcXbt::isDefault).findFirst().orElse(null);
                    if (defaultXbt == null) {
                        throw new IllegalArgumentException("Default Value Domain required.");
                    }
                }
                defaultValueDomainId = restri.getBdtScPriRestriId();
            }
        }

        if (defaultValueDomainId == null) {
            throw new IllegalArgumentException("Default Value Domain required.");
        }

        dslContext.update(BDT_SC_PRI_RESTRI).set(BDT_SC_PRI_RESTRI.IS_DEFAULT, (byte) 0)
                .where(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtScId)).execute();

        dslContext.update(BDT_SC_PRI_RESTRI).set(BDT_SC_PRI_RESTRI.IS_DEFAULT, (byte) 1)
                .where(BDT_SC_PRI_RESTRI.BDT_SC_PRI_RESTRI_ID.eq(ULong.valueOf(defaultValueDomainId))).execute();

        insertDerivedValueDomain(dtScId, insertedList);
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

    public DeleteDtScRepositoryResponse deleteDtSc(DeleteDtScRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        DtScManifestRecord dtScManifestRecord = dslContext.selectFrom(DT_SC_MANIFEST)
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(
                        ULong.valueOf(request.getDtScManifestId())
                ))
                .fetchOne();

        // Delete all DT_SCs derived from the target DT_SC in the request
        for (DtScManifestRecord derivedDtScManifestRecord : dslContext.selectFrom(DT_SC_MANIFEST)
                .where(DT_SC_MANIFEST.BASED_DT_SC_MANIFEST_ID.eq(dtScManifestRecord.getDtScManifestId()))
                .fetch()) {
            deleteDtSc(new DeleteDtScRepositoryRequest(request.getUser(), request.getLocalDateTime(),
                    derivedDtScManifestRecord.getDtScManifestId().toBigInteger()));
        }

        DtScRecord dtScRecord = dslContext.selectFrom(DT_SC)
                .where(DT_SC.DT_SC_ID.eq(dtScManifestRecord.getDtScId()))
                .fetchOne();

        DtManifestRecord dtManifestRecord = dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(dtScManifestRecord.getOwnerDtManifestId())).fetchOne();

        DtRecord dtRecord = dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(dtManifestRecord.getDtId())).fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(dtRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be deleted.");
        }

        if (!dtRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        int usedBieCount = dslContext.selectCount().from(BBIE_SC)
                .where(BBIE_SC.BASED_DT_SC_MANIFEST_ID.eq(dtScManifestRecord.getDtScManifestId())).fetchOne(0, int.class);

        if (usedBieCount > 0) {
            throw new IllegalArgumentException("This association used in " + usedBieCount + " BIE(s). Can not be deleted.");
        }

        // delete from Tables
        DtScManifestRecord prevDtScManifestRecord = null;
        DtScManifestRecord nextDtScManifestRecord = null;
        if (dtScManifestRecord.getPrevDtScManifestId() != null) {
            prevDtScManifestRecord = dslContext.selectFrom(DT_SC_MANIFEST)
                    .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(dtScManifestRecord.getPrevDtScManifestId()))
                    .fetchOne();
        }
        if (dtScManifestRecord.getNextDtScManifestId() != null) {
            nextDtScManifestRecord = dslContext.selectFrom(DT_SC_MANIFEST)
                    .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(dtScManifestRecord.getNextDtScManifestId()))
                    .fetchOne();
        }
        if (prevDtScManifestRecord != null) {
            if (nextDtScManifestRecord != null) {
                dslContext.update(DT_SC_MANIFEST)
                        .set(DT_SC_MANIFEST.NEXT_DT_SC_MANIFEST_ID, nextDtScManifestRecord.getDtScManifestId())
                        .execute();
            } else {
                dslContext.update(DT_SC_MANIFEST)
                        .setNull(DT_SC_MANIFEST.NEXT_DT_SC_MANIFEST_ID)
                        .execute();
            }
        }
        if (nextDtScManifestRecord != null) {
            if (prevDtScManifestRecord != null) {
                dslContext.update(DT_SC_MANIFEST)
                        .set(DT_SC_MANIFEST.PREV_DT_SC_MANIFEST_ID, prevDtScManifestRecord.getDtScManifestId())
                        .execute();
            } else {
                dslContext.update(DT_SC_MANIFEST)
                        .setNull(DT_SC_MANIFEST.PREV_DT_SC_MANIFEST_ID)
                        .execute();
            }
        }
        dtScManifestRecord.delete();

        if (dslContext.selectCount().from(DT_SC_MANIFEST)
                .where(DT_SC_MANIFEST.DT_SC_ID.eq(dtScManifestRecord.getDtScId()))
                .fetchOneInto(Integer.class) == 0) {
            
            dslContext.deleteFrom(BDT_SC_PRI_RESTRI)
                    .where(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(dtScRecord.getDtScId()))
                    .execute();

            DtScRecord prevDtScRecord = null;
            DtScRecord nextDtScRecord = null;
            if (dtScRecord.getPrevDtScId() != null) {
                prevDtScRecord = dslContext.selectFrom(DT_SC)
                        .where(DT_SC.DT_SC_ID.eq(dtScRecord.getPrevDtScId()))
                        .fetchOne();
            }
            if (dtScRecord.getNextDtScId() != null) {
                nextDtScRecord = dslContext.selectFrom(DT_SC)
                        .where(DT_SC.DT_SC_ID.eq(dtScRecord.getNextDtScId()))
                        .fetchOne();
            }
            if (prevDtScRecord != null) {
                if (nextDtScRecord != null) {
                    dslContext.update(DT_SC)
                            .set(DT_SC.NEXT_DT_SC_ID, nextDtScRecord.getDtScId())
                            .execute();
                } else {
                    dslContext.update(DT_SC)
                            .setNull(DT_SC.NEXT_DT_SC_ID)
                            .execute();
                }
            }
            if (nextDtScRecord != null) {
                if (prevDtScRecord != null) {
                    dslContext.update(DT_SC)
                            .set(DT_SC.PREV_DT_SC_ID, prevDtScRecord.getDtScId())
                            .execute();
                } else {
                    dslContext.update(DT_SC)
                            .setNull(DT_SC.PREV_DT_SC_ID)
                            .execute();
                }
            }
            dtScRecord.delete();
        }

        LogRecord logRecord =
                logRepository.insertBdtLog(
                        dtManifestRecord,
                        dtRecord,
                        LogAction.Modified,
                        userId, timestamp);
        dtManifestRecord.setLogId(logRecord.getLogId());
        dtManifestRecord.update(DT_MANIFEST.LOG_ID);

        return new DeleteDtScRepositoryResponse(dtScManifestRecord.getDtScManifestId().toBigInteger());
    }
}