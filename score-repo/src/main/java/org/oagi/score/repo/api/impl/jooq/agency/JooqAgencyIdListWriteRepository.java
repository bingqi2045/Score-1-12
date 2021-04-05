package org.oagi.score.repo.api.impl.jooq.agency;

import org.jooq.DSLContext;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.agency.AgencyIdListWriteRepository;
import org.oagi.score.repo.api.agency.model.AgencyIdList;
import org.oagi.score.repo.api.agency.model.ModifyAgencyIdListValuesRepositoryRequest;
import org.oagi.score.repo.api.agency.model.ModifyAgencyIdListValuesRepositoryResponse;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.corecomponent.model.CcState;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.repo.api.impl.jooq.log.LogAction;
import org.oagi.score.repo.api.impl.jooq.log.LogUtils;
import org.oagi.score.repo.api.impl.jooq.utils.ScoreGuidUtils;
import org.oagi.score.repo.api.user.model.ScoreRole;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.LOG;

public class JooqAgencyIdListWriteRepository
        extends JooqScoreRepository
        implements AgencyIdListWriteRepository {

    public JooqAgencyIdListWriteRepository(DSLContext dslContext) {
        super(dslContext);
    }

    @Override
    public BigInteger createAgencyIdList(ScoreUser user, BigInteger releaseId) throws ScoreDataAccessException {
        LocalDateTime timestamp = LocalDateTime.now();
        AgencyIdListRecord agencyIdListRecord = new AgencyIdListRecord();
        agencyIdListRecord.setGuid(ScoreGuidUtils.randomGuid());
        agencyIdListRecord.setEnumTypeGuid(ScoreGuidUtils.randomGuid());
        agencyIdListRecord.setName("AgencyIdentification");
        agencyIdListRecord.setState(CcState.WIP.name());
        agencyIdListRecord.setCreatedBy(ULong.valueOf(user.getUserId()));
        agencyIdListRecord.setCreationTimestamp(timestamp);
        agencyIdListRecord.setOwnerUserId(ULong.valueOf(user.getUserId()));
        agencyIdListRecord.setLastUpdatedBy(ULong.valueOf(user.getUserId()));
        agencyIdListRecord.setLastUpdateTimestamp(timestamp);
        agencyIdListRecord.setAgencyIdListId(dslContext().insertInto(AGENCY_ID_LIST).set(agencyIdListRecord)
                .returning(AGENCY_ID_LIST.AGENCY_ID_LIST_ID).fetchOne().getAgencyIdListId());

        AgencyIdListManifestRecord agencyIdListManifestRecord = new AgencyIdListManifestRecord();
        agencyIdListManifestRecord.setAgencyIdListId(agencyIdListRecord.getAgencyIdListId());
        agencyIdListManifestRecord.setReleaseId(ULong.valueOf(releaseId));
        agencyIdListManifestRecord.setAgencyIdListManifestId(dslContext().insertInto(AGENCY_ID_LIST_MANIFEST)
                .set(agencyIdListManifestRecord).returning(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID)
                .fetchOne().getAgencyIdListManifestId());

        LogRecord logRecord = insertAgencyIdListLog(agencyIdListManifestRecord, agencyIdListRecord, null, LogAction.Added, ULong.valueOf(user.getUserId()), timestamp);

        dslContext().update(AGENCY_ID_LIST_MANIFEST)
                .set(AGENCY_ID_LIST_MANIFEST.LOG_ID, logRecord.getLogId())
                .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(agencyIdListManifestRecord.getAgencyIdListManifestId()))
                .execute();

        return agencyIdListManifestRecord.getAgencyIdListManifestId().toBigInteger();
    }

    @Override
    public AgencyIdList updateAgencyIdListProperty(ScoreUser user, AgencyIdList agencyIdList) throws ScoreDataAccessException {
        ULong userId = ULong.valueOf(user.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        AgencyIdListManifestRecord agencyIdListManifestRecord = dslContext().selectFrom(AGENCY_ID_LIST_MANIFEST)
                .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(
                        ULong.valueOf(agencyIdList.getAgencyIdListManifestId())
                ))
                .fetchOne();

        AgencyIdListRecord agencyIdListRecord = dslContext().selectFrom(AGENCY_ID_LIST)
                .where(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(agencyIdListManifestRecord.getAgencyIdListId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(agencyIdListRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
        }

        if (!agencyIdListRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        agencyIdListRecord.setName(agencyIdList.getName());
        agencyIdListRecord.setVersionId(agencyIdList.getVersionId());
        agencyIdListRecord.setListId(agencyIdList.getListId());
        agencyIdListRecord.setNamespaceId((agencyIdList.getNamespaceId() != null) ? ULong.valueOf(agencyIdList.getNamespaceId()) : null);
        agencyIdListRecord.setDefinition(agencyIdList.getDefinition());
        agencyIdListRecord.setIsDeprecated((byte) (agencyIdList.isDeprecated() ? 1 : 0));
        agencyIdListRecord.setLastUpdatedBy(userId);
        agencyIdListRecord.setLastUpdateTimestamp(timestamp);
        agencyIdListRecord.update(AGENCY_ID_LIST.NAME,
                AGENCY_ID_LIST.VERSION_ID, AGENCY_ID_LIST.LIST_ID, AGENCY_ID_LIST.NAMESPACE_ID,
                AGENCY_ID_LIST.DEFINITION, AGENCY_ID_LIST.IS_DEPRECATED,
                AGENCY_ID_LIST.LAST_UPDATED_BY, AGENCY_ID_LIST.LAST_UPDATE_TIMESTAMP);

        // creates new revision for updated record.
        LogRecord logRecord =
                insertAgencyIdListLog(
                        agencyIdListManifestRecord,
                        agencyIdListRecord, agencyIdListManifestRecord.getLogId(),
                        LogAction.Modified,
                        userId, timestamp);

        agencyIdListManifestRecord.setLogId(logRecord.getLogId());
        agencyIdListManifestRecord.update(AGENCY_ID_LIST_MANIFEST.LOG_ID);

        return agencyIdList;
    }

    @Override
    public void transferOwnerShipAgencyIdList(ScoreUser user, BigInteger agencyIdListManifestId) throws ScoreDataAccessException {
    }

    @Override
    public void updateAgencyIdListState(ScoreUser user, BigInteger agencyIdListManifestId, CcState nextState) throws ScoreDataAccessException {
        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(user.getUserId());

        AgencyIdListManifestRecord agencyIdListManifestRecord = dslContext().selectFrom(AGENCY_ID_LIST_MANIFEST)
                .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(
                        ULong.valueOf(agencyIdListManifestId)
                ))
                .fetchOne();

        AgencyIdListRecord agencyIdListRecord = dslContext().selectFrom(AGENCY_ID_LIST)
                .where(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(agencyIdListManifestRecord.getAgencyIdListId()))
                .fetchOne();

        CcState prevState = CcState.valueOf(agencyIdListRecord.getState());

        if (!prevState.canMove(nextState)) {
            throw new IllegalArgumentException("The core component in '" + prevState + "' state cannot move to '" + nextState + "' state.");
        }

        if (!agencyIdListRecord.getOwnerUserId().equals(userId) && !prevState.canForceMove(nextState)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update agencyIdList state.
        agencyIdListRecord.setState(nextState.name());
        if (!prevState.canForceMove(nextState)) {
            agencyIdListRecord.setLastUpdatedBy(userId);
            agencyIdListRecord.setLastUpdateTimestamp(timestamp);
        }
        agencyIdListRecord.update(AGENCY_ID_LIST.STATE,
                AGENCY_ID_LIST.LAST_UPDATED_BY, AGENCY_ID_LIST.LAST_UPDATE_TIMESTAMP);

        // creates new revision for updated record.
        LogAction logAction = (CcState.Deleted == prevState && CcState.WIP == nextState)
                ? LogAction.Restored : LogAction.Modified;
        LogRecord logRecord =
                insertAgencyIdListLog(
                        agencyIdListManifestRecord,
                        agencyIdListRecord, agencyIdListManifestRecord.getLogId(),
                        logAction,
                        userId, timestamp);

        agencyIdListManifestRecord.setLogId(logRecord.getLogId());
        agencyIdListManifestRecord.update(AGENCY_ID_LIST_MANIFEST.LOG_ID);

    }

    @Override
    public void reviseAgencyIdList(ScoreUser user, BigInteger agencyIdListManifestId) throws ScoreDataAccessException {
        ULong userId = ULong.valueOf(user.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        AgencyIdListManifestRecord agencyIdListManifestRecord = dslContext().selectFrom(AGENCY_ID_LIST_MANIFEST)
                .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(
                        ULong.valueOf(agencyIdListManifestId)))
                .fetchOne();

        AgencyIdListRecord prevAgencyIdListRecord = dslContext().selectFrom(AGENCY_ID_LIST)
                .where(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(
                        agencyIdListManifestRecord.getAgencyIdListId()))
                .fetchOne();

        if (user.getRoles().contains(ScoreRole.DEVELOPER)) {
            if (!CcState.Published.equals(CcState.valueOf(prevAgencyIdListRecord.getState()))) {
                throw new IllegalArgumentException("Only the core component in 'Published' state can be revised.");
            }
        } else {
            if (!CcState.Production.equals(CcState.valueOf(prevAgencyIdListRecord.getState()))) {
                throw new IllegalArgumentException("Only the core component in 'Production' state can be revised.");
            }
        }

        ULong workingReleaseId = dslContext().select(RELEASE.RELEASE_ID)
                .from(RELEASE)
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchOneInto(ULong.class);

        ULong targetReleaseId = agencyIdListManifestRecord.getReleaseId();
        if (user.getRoles().contains(ScoreRole.DEVELOPER)) {
            if (!targetReleaseId.equals(workingReleaseId)) {
                throw new IllegalArgumentException("It only allows to revise the component in 'Working' branch for developers.");
            }
        } else {
            if (targetReleaseId.equals(workingReleaseId)) {
                throw new IllegalArgumentException("It only allows to revise the component in non-'Working' branch for end-users.");
            }
        }

        boolean ownerIsDeveloper = dslContext().select(APP_USER.IS_DEVELOPER)
                .from(APP_USER)
                .where(APP_USER.APP_USER_ID.eq(prevAgencyIdListRecord.getOwnerUserId()))
                .fetchOneInto(Boolean.class);

        if (user.getRoles().contains(ScoreRole.DEVELOPER) != ownerIsDeveloper) {
            throw new IllegalArgumentException("It only allows to revise the component for users in the same roles.");
        }

        AgencyIdListRecord nextAgencyIdListRecord = prevAgencyIdListRecord.copy();
        nextAgencyIdListRecord.setState(CcState.WIP.name());
        nextAgencyIdListRecord.setVersionId(nextAgencyIdListRecord.getVersionId());
        nextAgencyIdListRecord.setCreatedBy(userId);
        nextAgencyIdListRecord.setLastUpdatedBy(userId);
        nextAgencyIdListRecord.setOwnerUserId(userId);
        nextAgencyIdListRecord.setCreationTimestamp(timestamp);
        nextAgencyIdListRecord.setLastUpdateTimestamp(timestamp);
        nextAgencyIdListRecord.setPrevAgencyIdListId(prevAgencyIdListRecord.getAgencyIdListId());
        nextAgencyIdListRecord.setAgencyIdListId(
                dslContext().insertInto(AGENCY_ID_LIST)
                        .set(nextAgencyIdListRecord)
                        .returning(AGENCY_ID_LIST.AGENCY_ID_LIST_ID).fetchOne().getAgencyIdListId()
        );

        prevAgencyIdListRecord.setNextAgencyIdListId(nextAgencyIdListRecord.getAgencyIdListId());
        prevAgencyIdListRecord.update(AGENCY_ID_LIST.NEXT_AGENCY_ID_LIST_ID);

        createNewAgencyIdListValueForRevisedRecord(user, agencyIdListManifestRecord, nextAgencyIdListRecord, targetReleaseId, timestamp);

        // creates new revision for revised record.
        LogRecord logRecord =
                insertAgencyIdListLog(
                        agencyIdListManifestRecord,
                        nextAgencyIdListRecord, agencyIdListManifestRecord.getLogId(),
                        LogAction.Revised,
                        userId, timestamp);

        ULong responseAgencyIdListManifestId;
        agencyIdListManifestRecord.setAgencyIdListId(nextAgencyIdListRecord.getAgencyIdListId());
        agencyIdListManifestRecord.setLogId(logRecord.getLogId());
        agencyIdListManifestRecord.update(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID, AGENCY_ID_LIST_MANIFEST.LOG_ID);

        responseAgencyIdListManifestId = agencyIdListManifestRecord.getAgencyIdListManifestId();

        // #1094 keep update BIE's code list id
        updateBIEAgencyIdListId(agencyIdListManifestRecord.getReleaseId().toBigInteger(),
                prevAgencyIdListRecord.getAgencyIdListId().toBigInteger(),
                nextAgencyIdListRecord.getAgencyIdListId().toBigInteger());
    }

    @Override
    public void cancelAgencyIdList(ScoreUser user, BigInteger agencyIdListManifestId) throws ScoreDataAccessException {
    }

    public LogRecord insertAgencyIdListLog(AgencyIdListManifestRecord agencyIdListManifestRecord,
                                       AgencyIdListRecord agencyIdListRecord,
                                       ULong prevLogId,
                                       LogAction logAction,
                                       ULong requesterId,
                                       LocalDateTime timestamp) {

        LogRecord prevLogRecord = null;
        if (prevLogId != null) {
            prevLogRecord = dslContext().selectFrom(LOG)
                    .where(LOG.LOG_ID.eq(prevLogId))
                    .fetchOne();
        }

        LogRecord logRecord = new LogRecord();
        logRecord.setHash(LogUtils.generateHash());
        if (LogAction.Revised.equals(logAction)) {
            assert (prevLogRecord != null);
            logRecord.setRevisionNum(prevLogRecord.getRevisionNum().add(1));
            logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
        } else if (LogAction.Canceled.equals(logAction)) {
            assert (prevLogRecord != null);
            logRecord.setRevisionNum(prevLogRecord.getRevisionNum().subtract(1));
            logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
        } else {
            if (prevLogRecord != null) {
                logRecord.setRevisionNum(prevLogRecord.getRevisionNum());
                logRecord.setRevisionTrackingNum(prevLogRecord.getRevisionTrackingNum().add(1));
            } else {
                logRecord.setRevisionNum(UInteger.valueOf(1));
                logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
            }
        }
        logRecord.setLogAction(logAction.name());

        List<AgencyIdListValueManifestRecord> agencyIdListValueManifestRecords = dslContext().selectFrom(AGENCY_ID_LIST_VALUE_MANIFEST)
                .where(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(agencyIdListManifestRecord.getAgencyIdListManifestId()))
                .fetch();

        List<AgencyIdListValueRecord> agencyIdListValueRecords = dslContext().selectFrom(AGENCY_ID_LIST_VALUE)
                .where(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID.in(
                        agencyIdListValueManifestRecords.stream().map(e -> e.getAgencyIdListValueId()).collect(Collectors.toList())
                ))
                .fetch();

//        logRecord.setSnapshot(JSON.valueOf(serializer.serialize(agencyIdListManifestRecord, agencyIdListRecord,
//                agencyIdListValueManifestRecords, agencyIdListValueRecords)));
        logRecord.setReference(agencyIdListRecord.getGuid());
        logRecord.setCreatedBy(requesterId);
        logRecord.setCreationTimestamp(timestamp);
        if (prevLogRecord != null) {
            logRecord.setPrevLogId(prevLogRecord.getLogId());
        }

        logRecord.setLogId(dslContext().insertInto(LOG)
                .set(logRecord)
                .returning(LOG.LOG_ID).fetchOne().getLogId());
        if (prevLogRecord != null) {
            prevLogRecord.setNextLogId(logRecord.getLogId());
            prevLogRecord.update(LOG.NEXT_LOG_ID);
        }

        return logRecord;
    }

    public ModifyAgencyIdListValuesRepositoryResponse modifyAgencyIdListValues(ModifyAgencyIdListValuesRepositoryRequest request) {
        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(request.getRequester().getUserId());

        AgencyIdListManifestRecord agencyIdListManifestRecord = dslContext().selectFrom(AGENCY_ID_LIST_MANIFEST)
                .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(
                        ULong.valueOf(request.getAgencyIdListManifestId())
                ))
                .fetchOne();

        AgencyIdListRecord agencyIdListRecord = dslContext().selectFrom(AGENCY_ID_LIST)
                .where(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(agencyIdListManifestRecord.getAgencyIdListId()))
                .fetchOne();

        List<AgencyIdListValueManifestRecord> agencyIdListValueManifestRecordList =
                dslContext().selectFrom(AGENCY_ID_LIST_VALUE_MANIFEST)
                        .where(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(
                                ULong.valueOf(request.getAgencyIdListManifestId())))
                        .fetch();

        List<AgencyIdListValueRecord> agencyIdListValueRecordList =
                dslContext().selectFrom(AGENCY_ID_LIST_VALUE)
                        .where(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID.in(
                                agencyIdListValueManifestRecordList.stream()
                                        .map(e -> e.getAgencyIdListValueId()).collect(Collectors.toList()))
                        )
                        .fetch();

        // add
        addAgencyIdListValues(request.getRequester(), userId, timestamp,
                agencyIdListManifestRecord, agencyIdListRecord,
                request, agencyIdListValueManifestRecordList, agencyIdListValueRecordList);

        // update
        updateAgencyIdListValues(request.getRequester(), userId, timestamp,
                agencyIdListManifestRecord, agencyIdListRecord,
                request, agencyIdListValueManifestRecordList, agencyIdListValueRecordList);

        // delete
        deleteAgencyIdListValues(request.getRequester(), userId, timestamp,
                agencyIdListManifestRecord, agencyIdListRecord,
                request, agencyIdListValueManifestRecordList, agencyIdListValueRecordList);

        return new ModifyAgencyIdListValuesRepositoryResponse();
    }

    private void addAgencyIdListValues(
            ScoreUser user, ULong userId, LocalDateTime timestamp,
            AgencyIdListManifestRecord agencyIdListManifestRecord, AgencyIdListRecord agencyIdListRecord,
            ModifyAgencyIdListValuesRepositoryRequest request,
            List<AgencyIdListValueManifestRecord> agencyIdListValueManifestRecordList,
            List<AgencyIdListValueRecord> agencyIdListValueRecordList
    ) {
        Map<String, AgencyIdListValueRecord> agencyIdListValueRecordMapByValue =
                agencyIdListValueRecordList.stream()
                        .collect(Collectors.toMap(AgencyIdListValueRecord::getValue, Function.identity()));

        for (ModifyAgencyIdListValuesRepositoryRequest.AgencyIdListValue agencyIdListValue : request.getAgencyIdListValueList()) {
            if (agencyIdListValueRecordMapByValue.containsKey(agencyIdListValue.getValue())) {
                continue;
            }

            AgencyIdListValueRecord agencyIdListValueRecord = new AgencyIdListValueRecord();

            agencyIdListValueRecord.setOwnerListId(agencyIdListRecord.getAgencyIdListId());
            agencyIdListValueRecord.setGuid(ScoreGuidUtils.randomGuid());
            agencyIdListValueRecord.setName(agencyIdListValue.getName());
            agencyIdListValueRecord.setValue(agencyIdListValue.getValue());
            agencyIdListValueRecord.setDefinition(agencyIdListValue.getDefinition());
            agencyIdListValueRecord.setCreatedBy(userId);
            agencyIdListValueRecord.setOwnerUserId(userId);
            agencyIdListValueRecord.setLastUpdatedBy(userId);
            agencyIdListValueRecord.setCreationTimestamp(timestamp);
            agencyIdListValueRecord.setLastUpdateTimestamp(timestamp);
            agencyIdListValueRecord.setIsDeprecated((byte) (0));

            agencyIdListValueRecord.setAgencyIdListValueId(
                    dslContext().insertInto(AGENCY_ID_LIST_VALUE)
                            .set(agencyIdListValueRecord)
                            .returning(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID)
                            .fetchOne().getAgencyIdListValueId()
            );

            AgencyIdListValueManifestRecord agencyIdListValueManifestRecord = new AgencyIdListValueManifestRecord();

            agencyIdListValueManifestRecord.setReleaseId(agencyIdListManifestRecord.getReleaseId());
            agencyIdListValueManifestRecord.setAgencyIdListValueId(agencyIdListValueRecord.getAgencyIdListValueId());
            agencyIdListValueManifestRecord.setAgencyIdListManifestId(agencyIdListManifestRecord.getAgencyIdListManifestId());

            agencyIdListValueManifestRecord.setAgencyIdListValueManifestId(
                    dslContext().insertInto(AGENCY_ID_LIST_VALUE_MANIFEST)
                            .set(agencyIdListValueManifestRecord)
                            .returning(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_MANIFEST_ID)
                            .fetchOne().getAgencyIdListValueManifestId()
            );
        }
    }

    private void updateAgencyIdListValues(
            ScoreUser user, ULong userId, LocalDateTime timestamp,
            AgencyIdListManifestRecord agencyIdListManifestRecord, AgencyIdListRecord agencyIdListRecord,
            ModifyAgencyIdListValuesRepositoryRequest request,
            List<AgencyIdListValueManifestRecord> agencyIdListValueManifestRecordList,
            List<AgencyIdListValueRecord> agencyIdListValueRecordList
    ) {
        Map<String, AgencyIdListValueRecord> agencyIdListValueRecordMapByValue =
                agencyIdListValueRecordList.stream()
                        .collect(Collectors.toMap(AgencyIdListValueRecord::getValue, Function.identity()));

        for (ModifyAgencyIdListValuesRepositoryRequest.AgencyIdListValue agencyIdListValue : request.getAgencyIdListValueList()) {
            if (!agencyIdListValueRecordMapByValue.containsKey(agencyIdListValue.getValue())) {
                continue;
            }

            AgencyIdListValueRecord agencyIdListValueRecord = agencyIdListValueRecordMapByValue.get(agencyIdListValue.getValue());

            agencyIdListValueRecord.setName(agencyIdListValue.getName());
            agencyIdListValueRecord.setDefinition(agencyIdListValue.getDefinition());
            agencyIdListValueRecord.setIsDeprecated((byte) (agencyIdListValue.isDeprecated() ? 1 : 0));
            agencyIdListValueRecord.setLastUpdatedBy(userId);
            agencyIdListValueRecord.setLastUpdateTimestamp(timestamp);

            agencyIdListValueRecord.update(
                    AGENCY_ID_LIST_VALUE.NAME,
                    AGENCY_ID_LIST_VALUE.DEFINITION, AGENCY_ID_LIST_VALUE.IS_DEPRECATED,
                    AGENCY_ID_LIST_VALUE.LAST_UPDATED_BY, AGENCY_ID_LIST_VALUE.LAST_UPDATE_TIMESTAMP);
        }
    }

    private void deleteAgencyIdListValues(
            ScoreUser user, ULong userId, LocalDateTime timestamp,
            AgencyIdListManifestRecord agencyIdListManifestRecord, AgencyIdListRecord agencyIdListRecord,
            ModifyAgencyIdListValuesRepositoryRequest request,
            List<AgencyIdListValueManifestRecord> agencyIdListValueManifestRecordList,
            List<AgencyIdListValueRecord> agencyIdListValueRecordList
    ) {
        Map<String, AgencyIdListValueRecord> agencyIdListValueRecordMapByValue =
                agencyIdListValueRecordList.stream()
                        .collect(Collectors.toMap(AgencyIdListValueRecord::getValue, Function.identity()));

        for (ModifyAgencyIdListValuesRepositoryRequest.AgencyIdListValue agencyIdListValue : request.getAgencyIdListValueList()) {
            agencyIdListValueRecordMapByValue.remove(agencyIdListValue.getValue());
        }

        Map<ULong, AgencyIdListValueManifestRecord> agencyIdListValueManifestRecordMapById =
                agencyIdListValueManifestRecordList.stream()
                        .collect(Collectors.toMap(AgencyIdListValueManifestRecord::getAgencyIdListValueId, Function.identity()));

        for (AgencyIdListValueRecord agencyIdListValueRecord : agencyIdListValueRecordMapByValue.values()) {
            agencyIdListValueManifestRecordMapById.get(
                    agencyIdListValueRecord.getAgencyIdListValueId()
            ).delete();

            agencyIdListValueRecord.delete();
        }
    }

    private void updateBIEAgencyIdListId(BigInteger releaseId, BigInteger prevAgencyIdListId, BigInteger nextAgencyIdListId) {
        dslContext().update(BBIE)
                .set(BBIE.AGENCY_ID_LIST_ID, ULong.valueOf(nextAgencyIdListId))
                .where(BBIE.BBIE_ID.in(
                        dslContext().select(BBIE.BBIE_ID)
                                .from(BBIE)
                                .join(TOP_LEVEL_ASBIEP)
                                .on(BBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID))
                                .where(and(TOP_LEVEL_ASBIEP.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                                        BBIE.AGENCY_ID_LIST_ID.eq(ULong.valueOf(prevAgencyIdListId))))))
                .execute();

        dslContext().update(BBIE_SC)
                .set(BBIE_SC.AGENCY_ID_LIST_ID, ULong.valueOf(nextAgencyIdListId))
                .where(BBIE_SC.BBIE_SC_ID.in(
                        dslContext().select(BBIE_SC.BBIE_SC_ID)
                                .from(BBIE_SC)
                                .join(TOP_LEVEL_ASBIEP)
                                .on(BBIE_SC.OWNER_TOP_LEVEL_ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID))
                                .where(and(TOP_LEVEL_ASBIEP.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                                        BBIE_SC.AGENCY_ID_LIST_ID.eq(ULong.valueOf(prevAgencyIdListId))))))
                .execute();

    }

    private void discardLogAgencyIdListValues(AgencyIdListManifestRecord agencyIdListManifestRecord, AgencyIdListRecord agencyIdListRecord) {
        List<AgencyIdListValueManifestRecord> agencyIdListValueManifests = dslContext().selectFrom(AGENCY_ID_LIST_VALUE_MANIFEST)
                .where(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(agencyIdListManifestRecord.getAgencyIdListManifestId()))
                .fetch();

        for (AgencyIdListValueManifestRecord agencyIdListValueManifest : agencyIdListValueManifests) {
            AgencyIdListValueRecord agencyIdListValue = dslContext().selectFrom(AGENCY_ID_LIST_VALUE)
                    .where(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID.eq(agencyIdListValueManifest.getAgencyIdListValueId()))
                    .fetchOne();

            if (agencyIdListValue.getPrevAgencyIdListValueId() == null) {
                //delete code list value and code list manifest which added this revision
                agencyIdListValueManifest.delete();
                agencyIdListValue.delete();
            } else {
                //delete code list value and update code list value manifest
                AgencyIdListValueRecord prevAgencyIdListValue = dslContext().selectFrom(AGENCY_ID_LIST_VALUE)
                        .where(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID.eq(agencyIdListValue.getPrevAgencyIdListValueId()))
                        .fetchOne();
                prevAgencyIdListValue.setNextAgencyIdListValueId(null);
                prevAgencyIdListValue.update(AGENCY_ID_LIST_VALUE.NEXT_AGENCY_ID_LIST_VALUE_ID);
                agencyIdListValueManifest.setAgencyIdListValueId(prevAgencyIdListValue.getAgencyIdListValueId());
                agencyIdListValueManifest.update(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_ID);
                agencyIdListValue.delete();
            }
        }
    }

    private void createNewAgencyIdListValueForRevisedRecord(
            ScoreUser user,
            AgencyIdListManifestRecord manifestRecord,
            AgencyIdListRecord nextAgencyIdListRecord,
            ULong targetReleaseId,
            LocalDateTime timestamp) {
        for (AgencyIdListValueManifestRecord agencyIdListValueManifestRecord : dslContext().selectFrom(AGENCY_ID_LIST_VALUE_MANIFEST)
                .where(and(
                        AGENCY_ID_LIST_VALUE_MANIFEST.RELEASE_ID.eq(targetReleaseId),
                        AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(manifestRecord.getAgencyIdListManifestId())
                ))
                .fetch()) {

            AgencyIdListValueRecord prevAgencyIdListValueRecord = dslContext().selectFrom(AGENCY_ID_LIST_VALUE)
                    .where(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID.eq(agencyIdListValueManifestRecord.getAgencyIdListValueId()))
                    .fetchOne();

            AgencyIdListValueRecord nextAgencyIdListValueRecord = prevAgencyIdListValueRecord.copy();
            nextAgencyIdListValueRecord.setOwnerListId(nextAgencyIdListRecord.getAgencyIdListId());
            nextAgencyIdListValueRecord.setCreatedBy(ULong.valueOf(user.getUserId()));
            nextAgencyIdListValueRecord.setLastUpdatedBy(ULong.valueOf(user.getUserId()));
            nextAgencyIdListValueRecord.setOwnerUserId(ULong.valueOf(user.getUserId()));
            nextAgencyIdListValueRecord.setCreationTimestamp(timestamp);
            nextAgencyIdListValueRecord.setLastUpdateTimestamp(timestamp);
            nextAgencyIdListValueRecord.setPrevAgencyIdListValueId(prevAgencyIdListValueRecord.getAgencyIdListValueId());
            nextAgencyIdListValueRecord.setAgencyIdListValueId(
                    dslContext().insertInto(AGENCY_ID_LIST_VALUE)
                            .set(nextAgencyIdListValueRecord)
                            .returning(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID).fetchOne().getAgencyIdListValueId()
            );

            prevAgencyIdListValueRecord.setNextAgencyIdListValueId(nextAgencyIdListValueRecord.getAgencyIdListValueId());
            prevAgencyIdListValueRecord.update(AGENCY_ID_LIST_VALUE.NEXT_AGENCY_ID_LIST_VALUE_ID);

            agencyIdListValueManifestRecord.setAgencyIdListValueId(nextAgencyIdListValueRecord.getAgencyIdListValueId());
            agencyIdListValueManifestRecord.setAgencyIdListManifestId(manifestRecord.getAgencyIdListManifestId());
            agencyIdListValueManifestRecord.update(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_ID,
                    AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID);
        }
    }
}
