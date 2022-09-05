package org.oagi.score.repo.api.impl.jooq.agency;

import com.google.gson.Gson;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.types.UInteger;
import org.oagi.score.repo.api.agency.AgencyIdListWriteRepository;
import org.oagi.score.repo.api.agency.model.AgencyIdList;
import org.oagi.score.repo.api.agency.model.ModifyAgencyIdListValuesRepositoryRequest;
import org.oagi.score.repo.api.agency.model.ModifyAgencyIdListValuesRepositoryResponse;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.base.SortDirection;
import org.oagi.score.repo.api.corecomponent.model.CcState;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.repo.api.impl.jooq.log.LogAction;
import org.oagi.score.repo.api.impl.jooq.log.LogUtils;
import org.oagi.score.repo.api.impl.jooq.utils.ScoreGuidUtils;
import org.oagi.score.repo.api.user.model.ScoreRole;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.api.base.SortDirection.ASC;
import static org.oagi.score.repo.api.base.SortDirection.DESC;
import static org.oagi.score.repo.api.corecomponent.model.CcState.Deleted;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

public class JooqAgencyIdListWriteRepository
        extends JooqScoreRepository
        implements AgencyIdListWriteRepository {

    public JooqAgencyIdListWriteRepository(DSLContext dslContext) {
        super(dslContext);
    }

    @Override
    public String createAgencyIdList(ScoreUser user, String releaseId, String basedAgencyIdListManifestId) throws ScoreDataAccessException {
        String userId = user.getUserId();
        LocalDateTime timestamp = LocalDateTime.now();
        AgencyIdListRecord agencyIdListRecord = new AgencyIdListRecord();
        agencyIdListRecord.setAgencyIdListId(UUID.randomUUID().toString());
        agencyIdListRecord.setGuid(ScoreGuidUtils.randomGuid());
        agencyIdListRecord.setEnumTypeGuid(ScoreGuidUtils.randomGuid());
        agencyIdListRecord.setState(CcState.WIP.name());
        agencyIdListRecord.setCreatedBy(userId);
        agencyIdListRecord.setCreationTimestamp(timestamp);
        agencyIdListRecord.setOwnerUserId(userId);
        agencyIdListRecord.setListId(ScoreGuidUtils.randomGuid());
        agencyIdListRecord.setLastUpdatedBy(userId);
        agencyIdListRecord.setLastUpdateTimestamp(timestamp);

        AgencyIdListManifestRecord agencyIdListManifestRecord = new AgencyIdListManifestRecord();
        agencyIdListManifestRecord.setAgencyIdListManifestId(UUID.randomUUID().toString());
        AgencyIdListManifestRecord basedAgencyIdListManifest = null;
        AgencyIdListRecord basedAgencyIdList = null;

        if (basedAgencyIdListManifestId != null) {
            basedAgencyIdListManifest = dslContext().selectFrom(AGENCY_ID_LIST_MANIFEST)
                    .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(basedAgencyIdListManifestId))
                    .fetchOne();

            basedAgencyIdList = dslContext().selectFrom(AGENCY_ID_LIST)
                    .where(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(basedAgencyIdListManifest.getAgencyIdListId()))
                    .fetchOne();

            agencyIdListRecord.setName(basedAgencyIdList.getName());
            agencyIdListRecord.setVersionId(basedAgencyIdList.getVersionId());
            agencyIdListRecord.setBasedAgencyIdListId(basedAgencyIdList.getAgencyIdListId());

            agencyIdListManifestRecord.setReleaseId(basedAgencyIdListManifest.getReleaseId());
            agencyIdListManifestRecord.setBasedAgencyIdListManifestId(basedAgencyIdListManifest.getAgencyIdListManifestId());

        } else {
            agencyIdListRecord.setName("AgencyIdentification");
            agencyIdListManifestRecord.setReleaseId(releaseId);
        }

        dslContext().insertInto(AGENCY_ID_LIST)
                .set(agencyIdListRecord)
                .execute();

        agencyIdListManifestRecord.setAgencyIdListId(agencyIdListRecord.getAgencyIdListId());
        dslContext().insertInto(AGENCY_ID_LIST_MANIFEST)
                .set(agencyIdListManifestRecord)
                .execute();

        if (basedAgencyIdListManifest != null) {
            List<AgencyIdListValueManifestRecord> basedAgencyIdListValueManifestList =
                    dslContext().selectFrom(AGENCY_ID_LIST_VALUE_MANIFEST)
                            .where(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID
                                    .eq(basedAgencyIdListManifest.getAgencyIdListManifestId()))
                            .fetch();

            for (AgencyIdListValueManifestRecord basedAgencyIdListValueManifest : basedAgencyIdListValueManifestList) {
                AgencyIdListValueRecord basedAgencyIdListValue = dslContext().selectFrom(AGENCY_ID_LIST_VALUE)
                        .where(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID
                                .eq(basedAgencyIdListValueManifest.getAgencyIdListValueId()))
                        .fetchOne();

                AgencyIdListValueRecord agencyIdListValueRecord = basedAgencyIdListValue.copy();
                agencyIdListValueRecord.setAgencyIdListValueId(UUID.randomUUID().toString());
                agencyIdListValueRecord.setOwnerListId(agencyIdListRecord.getAgencyIdListId());
                agencyIdListValueRecord.setGuid(ScoreGuidUtils.randomGuid());
                agencyIdListValueRecord.setBasedAgencyIdListValueId(basedAgencyIdListValue.getAgencyIdListValueId());
                agencyIdListValueRecord.setCreatedBy(userId);
                agencyIdListValueRecord.setLastUpdatedBy(userId);
                agencyIdListValueRecord.setOwnerUserId(userId);
                agencyIdListValueRecord.setCreationTimestamp(timestamp);
                agencyIdListValueRecord.setLastUpdateTimestamp(timestamp);
                agencyIdListValueRecord.setPrevAgencyIdListValueId(null);
                agencyIdListValueRecord.setNextAgencyIdListValueId(null);

                dslContext().insertInto(AGENCY_ID_LIST_VALUE)
                        .set(agencyIdListValueRecord)
                        .execute();

                AgencyIdListValueManifestRecord agencyIdListValueManifestRecord = basedAgencyIdListValueManifest.copy();
                agencyIdListValueManifestRecord.setAgencyIdListValueManifestId(UUID.randomUUID().toString());
                agencyIdListValueManifestRecord.setReleaseId(basedAgencyIdListValueManifest.getReleaseId());
                agencyIdListValueManifestRecord.setAgencyIdListValueId(agencyIdListValueRecord.getAgencyIdListValueId());
                agencyIdListValueManifestRecord.setAgencyIdListManifestId(agencyIdListManifestRecord.getAgencyIdListManifestId());
                agencyIdListValueManifestRecord.setBasedAgencyIdListValueManifestId(basedAgencyIdListValueManifest.getAgencyIdListValueManifestId());
                agencyIdListValueManifestRecord.setPrevAgencyIdListValueManifestId(null);
                agencyIdListValueManifestRecord.setNextAgencyIdListValueManifestId(null);

                dslContext().insertInto(AGENCY_ID_LIST_VALUE_MANIFEST)
                        .set(agencyIdListValueManifestRecord)
                        .execute();

                if (basedAgencyIdListManifest.getAgencyIdListValueManifestId().equals(basedAgencyIdListValueManifest.getAgencyIdListValueManifestId())) {
                    agencyIdListRecord.setAgencyIdListValueId(agencyIdListValueRecord.getAgencyIdListValueId());
                    agencyIdListRecord.update();
                    agencyIdListManifestRecord.setAgencyIdListValueManifestId(agencyIdListValueManifestRecord.getAgencyIdListValueManifestId());
                    agencyIdListManifestRecord.update();
                }
            }
        }

        LogRecord logRecord = insertAgencyIdListLog(agencyIdListManifestRecord, agencyIdListRecord, null, LogAction.Added, user.getUserId(), timestamp);

        dslContext().update(AGENCY_ID_LIST_MANIFEST)
                .set(AGENCY_ID_LIST_MANIFEST.LOG_ID, logRecord.getLogId())
                .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(agencyIdListManifestRecord.getAgencyIdListManifestId()))
                .execute();

        return agencyIdListManifestRecord.getAgencyIdListManifestId();
    }

    @Override
    public AgencyIdList updateAgencyIdListProperty(ScoreUser user, AgencyIdList agencyIdList) throws ScoreDataAccessException {
        String userId = user.getUserId();
        LocalDateTime timestamp = LocalDateTime.now();

        AgencyIdListManifestRecord agencyIdListManifestRecord = dslContext().selectFrom(AGENCY_ID_LIST_MANIFEST)
                .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(
                        agencyIdList.getAgencyIdListManifestId()
                ))
                .fetchOne();

        AgencyIdListRecord agencyIdListRecord = dslContext().selectFrom(AGENCY_ID_LIST)
                .where(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(agencyIdListManifestRecord.getAgencyIdListId()))
                .fetchOne();

        List<AgencyIdListValueManifestRecord> agencyIdListValueManifestRecordList =
                dslContext().selectFrom(AGENCY_ID_LIST_VALUE_MANIFEST)
                        .where(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(
                                agencyIdListManifestRecord.getAgencyIdListManifestId()))
                        .fetch();

        List<AgencyIdListValueRecord> agencyIdListValueRecordList =
                dslContext().selectFrom(AGENCY_ID_LIST_VALUE)
                        .where(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID.in(
                                agencyIdListValueManifestRecordList.stream()
                                        .map(e -> e.getAgencyIdListValueId()).collect(Collectors.toList()))
                        )
                        .fetch();

        ModifyAgencyIdListValuesRepositoryRequest valueRequest = new ModifyAgencyIdListValuesRepositoryRequest();
        valueRequest.setAgencyIdListManifestId(agencyIdList.getAgencyIdListManifestId());
        valueRequest.setAgencyIdListValueList(agencyIdList.getValues().stream().map(e -> {
            ModifyAgencyIdListValuesRepositoryRequest.AgencyIdListValue agencyIdListValue =
                    new ModifyAgencyIdListValuesRepositoryRequest.AgencyIdListValue();

            agencyIdListValue.setAgencyIdListValueManifestId(e.getAgencyIdListValueManifestId());
            agencyIdListValue.setName(e.getName());
            agencyIdListValue.setValue(e.getValue());
            agencyIdListValue.setDefinition(e.getDefinition());
            agencyIdListValue.setDefinitionSource(e.getDefinitionSource());
            agencyIdListValue.setDeprecated(e.isDeprecated());

            return agencyIdListValue;
        }).collect(Collectors.toList()));

        // add
        addAgencyIdListValues(user, userId, timestamp,
                agencyIdListManifestRecord, agencyIdListRecord,
                valueRequest, agencyIdListValueManifestRecordList, agencyIdListValueRecordList);

        // update
        updateAgencyIdListValues(user, userId, timestamp,
                agencyIdListManifestRecord, agencyIdListRecord,
                valueRequest, agencyIdListValueManifestRecordList, agencyIdListValueRecordList);

        // delete
        deleteAgencyIdListValues(user, userId, timestamp,
                agencyIdListManifestRecord, agencyIdListRecord,
                valueRequest, agencyIdListValueManifestRecordList, agencyIdListValueRecordList);

        if (!CcState.WIP.equals(CcState.valueOf(agencyIdListRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
        }

        if (!agencyIdListRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        agencyIdListRecord.setName(agencyIdList.getName());
        agencyIdListRecord.setVersionId(agencyIdList.getVersionId());
        agencyIdListRecord.setListId(agencyIdList.getListId());
        agencyIdListRecord.setNamespaceId(agencyIdList.getNamespaceId());
        agencyIdListRecord.setDefinition(agencyIdList.getDefinition());
        agencyIdListRecord.setDefinitionSource(agencyIdList.getDefinitionSource());
        agencyIdListRecord.setRemark(agencyIdList.getRemark());
        agencyIdListRecord.setIsDeprecated((byte) (agencyIdList.isDeprecated() ? 1 : 0));
        agencyIdListRecord.setLastUpdatedBy(userId);
        agencyIdListRecord.setLastUpdateTimestamp(timestamp);

        if (agencyIdList.getAgencyIdListValueManifestId() != null) {
            AgencyIdListValueManifestRecord agencyIdListValueManifestRecord = dslContext()
                    .selectFrom(AGENCY_ID_LIST_VALUE_MANIFEST)
                    .where(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_MANIFEST_ID.eq(agencyIdList.getAgencyIdListValueManifestId())).fetchOne();
            agencyIdListRecord.setAgencyIdListValueId(agencyIdListValueManifestRecord.getAgencyIdListValueId());
            agencyIdListManifestRecord.setAgencyIdListValueManifestId(agencyIdListValueManifestRecord.getAgencyIdListValueManifestId());
        } else {
            agencyIdListRecord.setAgencyIdListValueId(null);
            agencyIdListManifestRecord.setAgencyIdListValueManifestId(null);
        }

        agencyIdListRecord.update(AGENCY_ID_LIST.NAME, AGENCY_ID_LIST.AGENCY_ID_LIST_VALUE_ID,
                AGENCY_ID_LIST.VERSION_ID, AGENCY_ID_LIST.LIST_ID, AGENCY_ID_LIST.NAMESPACE_ID,
                AGENCY_ID_LIST.DEFINITION, AGENCY_ID_LIST.DEFINITION_SOURCE, AGENCY_ID_LIST.REMARK,
                AGENCY_ID_LIST.IS_DEPRECATED, AGENCY_ID_LIST.LAST_UPDATED_BY, AGENCY_ID_LIST.LAST_UPDATE_TIMESTAMP);

        // creates new revision for updated record.
        LogRecord logRecord =
                insertAgencyIdListLog(
                        agencyIdListManifestRecord,
                        agencyIdListRecord, agencyIdListManifestRecord.getLogId(),
                        LogAction.Modified,
                        userId, timestamp);


        agencyIdListManifestRecord.setLogId(logRecord.getLogId());
        agencyIdListManifestRecord.update(AGENCY_ID_LIST_MANIFEST.LOG_ID, AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_VALUE_MANIFEST_ID);

        return agencyIdList;
    }

    @Override
    public void transferOwnershipAgencyIdList(ScoreUser user, String agencyIdListManifestId, String targetLoginId) throws ScoreDataAccessException {
        String userId = user.getUserId();
        LocalDateTime timestamp = LocalDateTime.now();
        String targetUserId = dslContext().selectFrom(APP_USER).where(APP_USER.LOGIN_ID.eq(targetLoginId)).fetchOne().getAppUserId();

        AgencyIdListManifestRecord agencyIdListManifestRecord = dslContext().selectFrom(AGENCY_ID_LIST_MANIFEST)
                .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(
                        agencyIdListManifestId
                ))
                .fetchOne();

        AgencyIdListRecord agencyIdListRecord = dslContext().selectFrom(AGENCY_ID_LIST)
                .where(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(agencyIdListManifestRecord.getAgencyIdListId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(agencyIdListRecord.getState()))) {
            throw new IllegalArgumentException("Only the code list in 'WIP' state can be modified.");
        }

        if (!agencyIdListRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the code list by the owner.");
        }

        agencyIdListRecord.setOwnerUserId(targetUserId);
        agencyIdListRecord.setLastUpdatedBy(userId);
        agencyIdListRecord.setLastUpdateTimestamp(timestamp);
        agencyIdListRecord.update(AGENCY_ID_LIST.OWNER_USER_ID, AGENCY_ID_LIST.LAST_UPDATED_BY, AGENCY_ID_LIST.LAST_UPDATE_TIMESTAMP);

        for (AgencyIdListValueManifestRecord agencyIdListValueManifest : dslContext().selectFrom(AGENCY_ID_LIST_VALUE_MANIFEST)
                .where(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(agencyIdListManifestRecord.getAgencyIdListManifestId()))
                .fetch()) {

            AgencyIdListValueRecord agencyIdListValueRecord = dslContext().selectFrom(AGENCY_ID_LIST_VALUE)
                    .where(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID.eq(agencyIdListValueManifest.getAgencyIdListValueId()))
                    .fetchOne();

            agencyIdListValueRecord.setOwnerUserId(targetUserId);
            agencyIdListValueRecord.update(AGENCY_ID_LIST_VALUE.OWNER_USER_ID);
        }

        LogRecord logRecord =
                insertAgencyIdListLog(
                        agencyIdListManifestRecord,
                        agencyIdListRecord, agencyIdListManifestRecord.getLogId(),
                        LogAction.Modified,
                        userId, timestamp);

        agencyIdListManifestRecord.setLogId(logRecord.getLogId());
        agencyIdListManifestRecord.update(AGENCY_ID_LIST_MANIFEST.LOG_ID);
    }

    @Override
    public void updateAgencyIdListState(ScoreUser user, String agencyIdListManifestId, CcState nextState) throws ScoreDataAccessException {
        LocalDateTime timestamp = LocalDateTime.now();
        String userId = user.getUserId();

        AgencyIdListManifestRecord agencyIdListManifestRecord = dslContext().selectFrom(AGENCY_ID_LIST_MANIFEST)
                .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(
                        agencyIdListManifestId
                ))
                .fetchOne();

        AgencyIdListRecord agencyIdListRecord = dslContext().selectFrom(AGENCY_ID_LIST)
                .where(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(agencyIdListManifestRecord.getAgencyIdListId()))
                .fetchOne();

        CcState prevState = CcState.valueOf(agencyIdListRecord.getState());

        if (!prevState.canMove(nextState)) {
            throw new IllegalArgumentException("The core component in '" + prevState + "' state cannot move to '" + nextState + "' state.");
        }

        if (prevState == Deleted) {
            boolean isOwnerDeveloper = dslContext().select(APP_USER.IS_DEVELOPER)
                    .from(APP_USER)
                    .where(APP_USER.APP_USER_ID.eq(agencyIdListRecord.getOwnerUserId()))
                    .fetchOneInto(Byte.class) == (byte) 1;
            boolean isRequesterDeveloper = dslContext().select(APP_USER.IS_DEVELOPER)
                    .from(APP_USER)
                    .where(APP_USER.APP_USER_ID.eq(userId))
                    .fetchOneInto(Byte.class) == (byte) 1;

            if (isOwnerDeveloper != isRequesterDeveloper) {
                if (isOwnerDeveloper) {
                    throw new IllegalArgumentException("Only developers can restore this component.");
                } else {
                    throw new IllegalArgumentException("Only end-users can restore this component.");
                }
            }
        } else {
            if (!agencyIdListRecord.getOwnerUserId().equals(userId) && !prevState.canForceMove(nextState)) {
                throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
            }
        }

        // update agencyIdList state.
        agencyIdListRecord.setState(nextState.name());
        if (!prevState.canForceMove(nextState)) {
            agencyIdListRecord.setLastUpdatedBy(userId);
            agencyIdListRecord.setLastUpdateTimestamp(timestamp);
        }
        if (prevState == Deleted) {
            agencyIdListRecord.setOwnerUserId(userId);
        }
        agencyIdListRecord.update(AGENCY_ID_LIST.STATE,
                AGENCY_ID_LIST.LAST_UPDATED_BY,
                AGENCY_ID_LIST.LAST_UPDATE_TIMESTAMP,
                AGENCY_ID_LIST.OWNER_USER_ID);

        // creates new revision for updated record.
        LogAction logAction = (Deleted == prevState && CcState.WIP == nextState)
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
    public void reviseAgencyIdList(ScoreUser user, String agencyIdListManifestId) throws ScoreDataAccessException {
        String userId = user.getUserId();
        LocalDateTime timestamp = LocalDateTime.now();

        AgencyIdListManifestRecord agencyIdListManifestRecord = dslContext().selectFrom(AGENCY_ID_LIST_MANIFEST)
                .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(
                        agencyIdListManifestId))
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

        String workingReleaseId = dslContext().select(RELEASE.RELEASE_ID)
                .from(RELEASE)
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchOneInto(String.class);

        String targetReleaseId = agencyIdListManifestRecord.getReleaseId();
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
        nextAgencyIdListRecord.setAgencyIdListId(UUID.randomUUID().toString());
        nextAgencyIdListRecord.setState(CcState.WIP.name());
        nextAgencyIdListRecord.setVersionId(nextAgencyIdListRecord.getVersionId());
        nextAgencyIdListRecord.setCreatedBy(userId);
        nextAgencyIdListRecord.setLastUpdatedBy(userId);
        nextAgencyIdListRecord.setOwnerUserId(userId);
        nextAgencyIdListRecord.setCreationTimestamp(timestamp);
        nextAgencyIdListRecord.setLastUpdateTimestamp(timestamp);
        nextAgencyIdListRecord.setPrevAgencyIdListId(prevAgencyIdListRecord.getAgencyIdListId());
        dslContext().insertInto(AGENCY_ID_LIST)
                .set(nextAgencyIdListRecord)
                .execute();

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

        agencyIdListManifestRecord.setAgencyIdListId(nextAgencyIdListRecord.getAgencyIdListId());
        agencyIdListManifestRecord.setLogId(logRecord.getLogId());
        agencyIdListManifestRecord.update(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID, AGENCY_ID_LIST_MANIFEST.LOG_ID);

        if (agencyIdListManifestRecord.getAgencyIdListValueManifestId() != null) {
            nextAgencyIdListRecord.setAgencyIdListValueId(dslContext().select(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_ID)
                    .from(AGENCY_ID_LIST_VALUE_MANIFEST)
                    .where(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_MANIFEST_ID.eq(agencyIdListManifestRecord.getAgencyIdListValueManifestId()))
                    .fetchOneInto(String.class));
            nextAgencyIdListRecord.update(AGENCY_ID_LIST.AGENCY_ID_LIST_VALUE_ID);
        }

        // #1094 keep update BIE's code list id
        updateBIEAgencyIdListId(agencyIdListManifestRecord.getReleaseId(),
                prevAgencyIdListRecord.getAgencyIdListId(),
                nextAgencyIdListRecord.getAgencyIdListId());
    }

    @Override
    public void cancelAgencyIdList(ScoreUser user, String agencyIdListManifestId) throws ScoreDataAccessException {

        AgencyIdListManifestRecord agencyIdListManifestRecord = dslContext().selectFrom(AGENCY_ID_LIST_MANIFEST)
                .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(agencyIdListManifestId)).fetchOne();

        if (agencyIdListManifestRecord == null) {
            throw new IllegalArgumentException("Not found a target Agency Id List");
        }

        AgencyIdListRecord agencyIdListRecord = dslContext().selectFrom(AGENCY_ID_LIST)
                .where(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(agencyIdListManifestRecord.getAgencyIdListId())).fetchOne();

        if (agencyIdListRecord.getPrevAgencyIdListId() == null) {
            throw new IllegalArgumentException("Not found previous revision");
        }

        AgencyIdListRecord prevAgencyIdListRecord = dslContext().selectFrom(AGENCY_ID_LIST)
                .where(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(agencyIdListRecord.getPrevAgencyIdListId())).fetchOne();

        // update AGENCY ID LIST MANIFEST's agencyIdList_id and revision_id
        agencyIdListManifestRecord.setAgencyIdListId(agencyIdListRecord.getPrevAgencyIdListId());
        agencyIdListManifestRecord.update(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID);

        // #1094 keep update BIE's agency id list id
        updateBIEAgencyIdListId(agencyIdListManifestRecord.getReleaseId(),
                agencyIdListRecord.getAgencyIdListId(),
                agencyIdListRecord.getPrevAgencyIdListId());

        agencyIdListRecord.setAgencyIdListValueId(null);
        agencyIdListRecord.update(AGENCY_ID_LIST.AGENCY_ID_LIST_VALUE_ID);

        discardLogAgencyIdListValues(agencyIdListManifestRecord, agencyIdListRecord);

        // unlink prev AGENCY_ID_LIST
        prevAgencyIdListRecord.setNextAgencyIdListId(null);
        prevAgencyIdListRecord.update(AGENCY_ID_LIST.NEXT_AGENCY_ID_LIST_ID);

        // clean logs up
        String reference = dslContext().select(LOG.REFERENCE)
                .from(LOG)
                .where(LOG.LOG_ID.eq(agencyIdListManifestRecord.getLogId()))
                .fetchOneInto(String.class);

        agencyIdListManifestRecord.setLogId(null);
        agencyIdListManifestRecord.update(AGENCY_ID_LIST_MANIFEST.LOG_ID);

        LogRecord logRecord = revertToStableStateByReference(reference);
        agencyIdListManifestRecord.setLogId(logRecord.getLogId());
        agencyIdListManifestRecord.update(AGENCY_ID_LIST_MANIFEST.LOG_ID);

        if (prevAgencyIdListRecord.getAgencyIdListValueId() != null) {
            agencyIdListManifestRecord.setAgencyIdListValueManifestId(
                    dslContext().select(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_MANIFEST_ID)
                            .from(AGENCY_ID_LIST_VALUE_MANIFEST)
                            .where(and(AGENCY_ID_LIST_VALUE_MANIFEST.RELEASE_ID.eq(agencyIdListManifestRecord.getReleaseId()),
                                    AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_ID.eq(prevAgencyIdListRecord.getAgencyIdListValueId())
                            )).fetchOneInto(String.class));
        }

        // delete current AGENCY_ID_LIST
        agencyIdListRecord.delete();
    }

    public LogRecord revertToStableStateByReference(String reference) {
        List<LogRecord> logRecordList = getSortedLogListByReference(reference, SortDirection.DESC);

        LogRecord logRecordInStableState = null;
        List<String> deleteTargetLogIdList = new ArrayList();
        int revisionNum = -1;
        for (int i = 0, len = logRecordList.size(); i < len; ++i) {
            LogRecord logRecord = logRecordList.get(i);
            if (revisionNum < 0) {
                revisionNum = logRecord.getRevisionNum().intValue();
            } else {
                if (logRecord.getRevisionNum().intValue() < revisionNum) {
                    logRecordInStableState = logRecord;
                    break;
                }
            }
            deleteTargetLogIdList.add(logRecord.getLogId());
        }

        if (logRecordInStableState == null) {
            throw new IllegalStateException();
        }

        // To avoid a foreign key constraint
        dslContext().update(LOG)
                .setNull(LOG.NEXT_LOG_ID)
                .where(LOG.LOG_ID.eq(logRecordInStableState.getLogId()))
                .execute();
        logRecordInStableState.setNextLogId(null);

        dslContext().update(LOG)
                .setNull(LOG.PREV_LOG_ID)
                .setNull(LOG.NEXT_LOG_ID)
                .where(
                        deleteTargetLogIdList.size() == 1 ?
                                LOG.LOG_ID.eq(deleteTargetLogIdList.get(0)) :
                                LOG.LOG_ID.in(deleteTargetLogIdList)
                )
                .execute();

        dslContext().deleteFrom(LOG)
                .where(
                        deleteTargetLogIdList.size() == 1 ?
                                LOG.LOG_ID.eq(deleteTargetLogIdList.get(0)) :
                                LOG.LOG_ID.in(deleteTargetLogIdList)
                )
                .execute();

        return logRecordInStableState;
    }

    public List<LogRecord> getSortedLogListByReference(String reference, SortDirection sortDirection) {
        List<LogRecord> logRecordList =
                dslContext().select(LOG.LOG_ID, LOG.HASH, LOG.REVISION_NUM, LOG.REVISION_TRACKING_NUM,
                        LOG.LOG_ACTION, LOG.REFERENCE, LOG.PREV_LOG_ID, LOG.NEXT_LOG_ID,
                        LOG.CREATED_BY, LOG.CREATION_TIMESTAMP)
                        .from(LOG)
                        .where(LOG.REFERENCE.eq(reference))
                        .fetchInto(LogRecord.class);

        List<LogRecord> sortedLogRecordList = new ArrayList(logRecordList.size());
        if (logRecordList.size() > 0) {
            Map<String, LogRecord> logRecordMap = logRecordList.stream()
                    .collect(Collectors.toMap(LogRecord::getLogId, Function.identity()));

            if (sortDirection == ASC) {
                LogRecord log =
                        logRecordList.stream().filter(e -> e.getPrevLogId() == null).findFirst().get();
                while (log != null) {
                    sortedLogRecordList.add(log);
                    if (log.getNextLogId() != null) {
                        log = logRecordMap.get(log.getNextLogId());
                    } else {
                        log = null;
                    }
                }
            } else if (sortDirection == DESC) {
                LogRecord log =
                        logRecordList.stream().filter(e -> e.getNextLogId() == null).findFirst().get();
                while (log != null) {
                    sortedLogRecordList.add(log);
                    if (log.getPrevLogId() != null) {
                        log = logRecordMap.get(log.getPrevLogId());
                    } else {
                        log = null;
                    }
                }
            }
        }
        return sortedLogRecordList;
    }

    public LogRecord insertAgencyIdListLog(AgencyIdListManifestRecord agencyIdListManifestRecord,
                                       AgencyIdListRecord agencyIdListRecord,
                                       String prevLogId,
                                       LogAction logAction,
                                       String requesterId,
                                       LocalDateTime timestamp) {

        LogRecord prevLogRecord = null;
        if (prevLogId != null) {
            prevLogRecord = dslContext().selectFrom(LOG)
                    .where(LOG.LOG_ID.eq(prevLogId))
                    .fetchOne();
        }

        LogRecord logRecord = new LogRecord();
        logRecord.setLogId(UUID.randomUUID().toString());
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

        logRecord.setSnapshot(JSON.valueOf(serialize(agencyIdListManifestRecord, agencyIdListRecord,
                agencyIdListValueManifestRecords, agencyIdListValueRecords)));
        logRecord.setReference(agencyIdListRecord.getGuid());
        logRecord.setCreatedBy(requesterId);
        logRecord.setCreationTimestamp(timestamp);
        if (prevLogRecord != null) {
            logRecord.setPrevLogId(prevLogRecord.getLogId());
        }

        dslContext().insertInto(LOG)
                .set(logRecord)
                .execute();
        if (prevLogRecord != null) {
            prevLogRecord.setNextLogId(logRecord.getLogId());
            prevLogRecord.update(LOG.NEXT_LOG_ID);
        }

        return logRecord;
    }

    private String serialize(AgencyIdListManifestRecord agencyIdListManifestRecord, AgencyIdListRecord agencyIdListRecord,
                            List<AgencyIdListValueManifestRecord> agencyIdListValueManifestRecords, List<AgencyIdListValueRecord> agencyIdListValueRecords) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "agencyIdList");
        properties.put("guid", agencyIdListRecord.getGuid());
        properties.put("enumTypeGuid", agencyIdListRecord.getEnumTypeGuid());
        if (agencyIdListRecord.getAgencyIdListValueId() != null) {
            AgencyIdListValueRecord agencyIdListValueRecord = dslContext().selectFrom(AGENCY_ID_LIST_VALUE)
                    .where(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID.eq(agencyIdListRecord.getAgencyIdListValueId()))
                    .fetchOptional().orElse(null);
            Map<String, Object> userProperties = new HashMap();
            userProperties.put("guid", agencyIdListValueRecord.getGuid());
            userProperties.put("name", agencyIdListValueRecord.getName());
            properties.put("agencyIdListValue", userProperties);

        }
        properties.put("name", agencyIdListRecord.getName());
        properties.put("listId", agencyIdListRecord.getListId());
        properties.put("versionId", agencyIdListRecord.getVersionId());
        properties.put("definition", agencyIdListRecord.getDefinition());
        properties.put("definitionSource", agencyIdListRecord.getDefinitionSource());
        properties.put("remark", agencyIdListRecord.getRemark());
        properties.put("state", agencyIdListRecord.getState());
        properties.put("deprecated", (byte) 1 == agencyIdListRecord.getIsDeprecated());

        if (agencyIdListRecord.getBasedAgencyIdListId() != null) {
            AgencyIdListRecord based = dslContext().selectFrom(AGENCY_ID_LIST)
                    .where(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(agencyIdListRecord.getBasedAgencyIdListId()))
                    .fetchOptional().orElse(null);
            Map<String, Object> userProperties = new HashMap();
            userProperties.put("guid", based.getGuid());
            userProperties.put("name", based.getName());
            userProperties.put("listId", based.getListId());
            userProperties.put("versionId", based.getVersionId());
            properties.put("basedAgencyIdList", userProperties);
        }

        AppUserRecord userRecord = dslContext().selectFrom(APP_USER)
                .where(APP_USER.APP_USER_ID.eq(agencyIdListRecord.getOwnerUserId()))
                .fetchOptional().orElse(null);

        Map<String, Object> ownerProperties = new HashMap();
        ownerProperties.put("username", userRecord.getLoginId());
        ownerProperties.put("roles", Arrays.asList(((byte) 1 == userRecord.getIsDeveloper()) ?
                "developer" : "end-user"));
        properties.put("ownerUser", ownerProperties);

        if (agencyIdListRecord.getNamespaceId() != null) {
            NamespaceRecord namespaceRecord = dslContext().selectFrom(NAMESPACE)
                    .where(NAMESPACE.NAMESPACE_ID.eq(agencyIdListRecord.getNamespaceId()))
                    .fetchOptional().orElse(null);

            Map<String, Object> userProperties = new HashMap();
            userProperties.put("uri", namespaceRecord.getUri());
            userProperties.put("standard", (byte) 1 == namespaceRecord.getIsStdNmsp());
            properties.put("namespace", userProperties);
        }

        List<Map<String, Object>> values = new ArrayList();
        Map<String, AgencyIdListValueRecord> agencyIdListValueRecordMap = agencyIdListValueRecords.stream().collect(
                Collectors.toMap(AgencyIdListValueRecord::getAgencyIdListValueId, Function.identity()));
        for (AgencyIdListValueManifestRecord agencyIdListValueManifestRecord : agencyIdListValueManifestRecords) {
            AgencyIdListValueRecord agencyIdListValueRecord = agencyIdListValueRecordMap.get(agencyIdListValueManifestRecord.getAgencyIdListValueId());
            values.add(serialize(agencyIdListValueManifestRecord, agencyIdListValueRecord));
        }
        properties.put("values", values);

        Gson gson = new Gson();
        return gson.toJson(properties, HashMap.class);
    }

    private Map<String, Object> serialize(AgencyIdListValueManifestRecord agencyIdListValueManifestRecord,
                                          AgencyIdListValueRecord agencyIdListValueRecord) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "agencyIdListValue");
        properties.put("guid", agencyIdListValueRecord.getGuid());
        properties.put("value", agencyIdListValueRecord.getValue());
        properties.put("name", agencyIdListValueRecord.getName());
        properties.put("definition", agencyIdListValueRecord.getDefinition());
        properties.put("definitionSource", agencyIdListValueRecord.getDefinitionSource());
        properties.put("deprecated", (byte) 1 == agencyIdListValueRecord.getIsDeprecated());

        return properties;
    }

    public ModifyAgencyIdListValuesRepositoryResponse modifyAgencyIdListValues(ModifyAgencyIdListValuesRepositoryRequest request) {
        LocalDateTime timestamp = LocalDateTime.now();
        String userId = request.getRequester().getUserId();

        AgencyIdListManifestRecord agencyIdListManifestRecord = dslContext().selectFrom(AGENCY_ID_LIST_MANIFEST)
                .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(
                        request.getAgencyIdListManifestId()
                ))
                .fetchOne();

        AgencyIdListRecord agencyIdListRecord = dslContext().selectFrom(AGENCY_ID_LIST)
                .where(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(agencyIdListManifestRecord.getAgencyIdListId()))
                .fetchOne();

        List<AgencyIdListValueManifestRecord> agencyIdListValueManifestRecordList =
                dslContext().selectFrom(AGENCY_ID_LIST_VALUE_MANIFEST)
                        .where(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(
                                request.getAgencyIdListManifestId()))
                        .fetch();

        List<AgencyIdListValueRecord> agencyIdListValueRecordList =
                dslContext().selectFrom(AGENCY_ID_LIST_VALUE)
                        .where(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID.in(
                                agencyIdListValueManifestRecordList.stream()
                                        .map(e -> e.getAgencyIdListValueId()).collect(Collectors.toList()))
                        )
                        .fetch();

        // delete
        deleteAgencyIdListValues(request.getRequester(), userId, timestamp,
                agencyIdListManifestRecord, agencyIdListRecord,
                request, agencyIdListValueManifestRecordList, agencyIdListValueRecordList);

        // add
        addAgencyIdListValues(request.getRequester(), userId, timestamp,
                agencyIdListManifestRecord, agencyIdListRecord,
                request, agencyIdListValueManifestRecordList, agencyIdListValueRecordList);

        // update
        updateAgencyIdListValues(request.getRequester(), userId, timestamp,
                agencyIdListManifestRecord, agencyIdListRecord,
                request, agencyIdListValueManifestRecordList, agencyIdListValueRecordList);

        return new ModifyAgencyIdListValuesRepositoryResponse();
    }

    private void addAgencyIdListValues(
            ScoreUser user, String userId, LocalDateTime timestamp,
            AgencyIdListManifestRecord agencyIdListManifestRecord, AgencyIdListRecord agencyIdListRecord,
            ModifyAgencyIdListValuesRepositoryRequest request,
            List<AgencyIdListValueManifestRecord> agencyIdListValueManifestRecordList,
            List<AgencyIdListValueRecord> agencyIdListValueRecordList
    ) {
        for (ModifyAgencyIdListValuesRepositoryRequest.AgencyIdListValue agencyIdListValue : request.getAgencyIdListValueList()) {
            if (agencyIdListValue.getAgencyIdListValueManifestId() != null) {
                continue;
            }

            AgencyIdListValueRecord agencyIdListValueRecord = new AgencyIdListValueRecord();

            agencyIdListValueRecord.setAgencyIdListValueId(UUID.randomUUID().toString());
            agencyIdListValueRecord.setOwnerListId(agencyIdListRecord.getAgencyIdListId());
            agencyIdListValueRecord.setGuid(ScoreGuidUtils.randomGuid());
            agencyIdListValueRecord.setName(agencyIdListValue.getName());
            agencyIdListValueRecord.setValue(agencyIdListValue.getValue());
            agencyIdListValueRecord.setDefinition(agencyIdListValue.getDefinition());
            agencyIdListValueRecord.setDefinitionSource(agencyIdListValue.getDefinitionSource());
            agencyIdListValueRecord.setCreatedBy(userId);
            agencyIdListValueRecord.setOwnerUserId(userId);
            agencyIdListValueRecord.setLastUpdatedBy(userId);
            agencyIdListValueRecord.setCreationTimestamp(timestamp);
            agencyIdListValueRecord.setLastUpdateTimestamp(timestamp);
            agencyIdListValueRecord.setIsDeprecated((byte) 0);

            dslContext().insertInto(AGENCY_ID_LIST_VALUE)
                    .set(agencyIdListValueRecord)
                    .execute();

            AgencyIdListValueManifestRecord agencyIdListValueManifestRecord = new AgencyIdListValueManifestRecord();

            agencyIdListValueManifestRecord.setAgencyIdListValueManifestId(UUID.randomUUID().toString());
            agencyIdListValueManifestRecord.setReleaseId(agencyIdListManifestRecord.getReleaseId());
            agencyIdListValueManifestRecord.setAgencyIdListValueId(agencyIdListValueRecord.getAgencyIdListValueId());
            agencyIdListValueManifestRecord.setAgencyIdListManifestId(agencyIdListManifestRecord.getAgencyIdListManifestId());

            dslContext().insertInto(AGENCY_ID_LIST_VALUE_MANIFEST)
                    .set(agencyIdListValueManifestRecord)
                    .execute();
        }
    }

    private void updateAgencyIdListValues(
            ScoreUser user, String userId, LocalDateTime timestamp,
            AgencyIdListManifestRecord agencyIdListManifestRecord, AgencyIdListRecord agencyIdListRecord,
            ModifyAgencyIdListValuesRepositoryRequest request,
            List<AgencyIdListValueManifestRecord> agencyIdListValueManifestRecordList,
            List<AgencyIdListValueRecord> agencyIdListValueRecordList
    ) {
        Map<String, AgencyIdListValueManifestRecord> agencyIdListValueManifestRecordMap =
                agencyIdListValueManifestRecordList.stream()
                        .collect(Collectors.toMap(AgencyIdListValueManifestRecord::getAgencyIdListValueManifestId, Function.identity()));
        Map<String, AgencyIdListValueRecord> agencyIdListValueRecordMap =
                agencyIdListValueRecordList.stream()
                        .collect(Collectors.toMap(AgencyIdListValueRecord::getAgencyIdListValueId, Function.identity()));

        for (ModifyAgencyIdListValuesRepositoryRequest.AgencyIdListValue agencyIdListValue : request.getAgencyIdListValueList()) {
            if (agencyIdListValue.getAgencyIdListValueManifestId() == null) {
                continue;
            }

            AgencyIdListValueManifestRecord agencyIdListValueManifestRecord =
                    agencyIdListValueManifestRecordMap.get(agencyIdListValue.getAgencyIdListValueManifestId());
            AgencyIdListValueRecord agencyIdListValueRecord =
                    agencyIdListValueRecordMap.get(agencyIdListValueManifestRecord.getAgencyIdListValueId());

            agencyIdListValueRecord.setValue(agencyIdListValue.getValue());
            agencyIdListValueRecord.setName(agencyIdListValue.getName());
            agencyIdListValueRecord.setDefinition(agencyIdListValue.getDefinition());
            agencyIdListValueRecord.setDefinitionSource(agencyIdListValue.getDefinitionSource());
            agencyIdListValueRecord.setIsDeprecated((byte) (agencyIdListValue.isDeprecated() ? 1 : 0));
            agencyIdListValueRecord.setLastUpdatedBy(userId);
            agencyIdListValueRecord.setLastUpdateTimestamp(timestamp);

            agencyIdListValueRecord.update(AGENCY_ID_LIST_VALUE.VALUE,
                    AGENCY_ID_LIST_VALUE.NAME, AGENCY_ID_LIST_VALUE.IS_DEPRECATED,
                    AGENCY_ID_LIST_VALUE.DEFINITION, AGENCY_ID_LIST_VALUE.DEFINITION_SOURCE,
                    AGENCY_ID_LIST_VALUE.LAST_UPDATED_BY, AGENCY_ID_LIST_VALUE.LAST_UPDATE_TIMESTAMP);
        }
    }

    private void deleteAgencyIdListValues(
            ScoreUser user, String userId, LocalDateTime timestamp,
            AgencyIdListManifestRecord agencyIdListManifestRecord, AgencyIdListRecord agencyIdListRecord,
            ModifyAgencyIdListValuesRepositoryRequest request,
            List<AgencyIdListValueManifestRecord> agencyIdListValueManifestRecordList,
            List<AgencyIdListValueRecord> agencyIdListValueRecordList
    ) {
        List<String> agencyIdListValueManifestIdListInRequest = request.getAgencyIdListValueList()
                .stream().filter(e -> e.getAgencyIdListValueManifestId() != null)
                .map(e -> e.getAgencyIdListValueManifestId()).collect(Collectors.toList());

        List<AgencyIdListValueManifestRecord> deletedAgencyIdListValueManifestList = agencyIdListValueManifestRecordList
                .stream().filter(e -> !agencyIdListValueManifestIdListInRequest.contains(e.getAgencyIdListValueManifestId())).collect(Collectors.toList());

        if (!deletedAgencyIdListValueManifestList.isEmpty()) {
            dslContext().deleteFrom(AGENCY_ID_LIST_VALUE_MANIFEST)
                    .where(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_MANIFEST_ID.in(deletedAgencyIdListValueManifestList.stream().map(e -> e.getAgencyIdListValueManifestId()).collect(Collectors.toList())))
                    .execute();

            dslContext().deleteFrom(AGENCY_ID_LIST_VALUE)
                    .where(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID.in(deletedAgencyIdListValueManifestList.stream().map(e -> e.getAgencyIdListValueId()).collect(Collectors.toList())))
                    .execute();
        }
    }

    private void updateCodeListAgencyIdListValueId(String releaseId, String prevAgencyIdListValueId, String nextAgencyIdListValueId) {
        dslContext().update(CODE_LIST)
                .set(CODE_LIST.AGENCY_ID_LIST_VALUE_ID, nextAgencyIdListValueId)
                .where(CODE_LIST.CODE_LIST_ID.in(
                        dslContext().select(CODE_LIST.CODE_LIST_ID)
                                .from(CODE_LIST)
                                .join(CODE_LIST_MANIFEST)
                                .on(CODE_LIST.CODE_LIST_ID.eq(CODE_LIST_MANIFEST.CODE_LIST_ID))
                                .where(and(CODE_LIST_MANIFEST.RELEASE_ID.eq(releaseId),
                                        CODE_LIST.AGENCY_ID_LIST_VALUE_ID.eq(prevAgencyIdListValueId)))))
                .execute();
    }

    private void updateBIEAgencyIdListId(String releaseId, String prevAgencyIdListId, String nextAgencyIdListId) {
        dslContext().update(BBIE)
                .set(BBIE.AGENCY_ID_LIST_ID, nextAgencyIdListId)
                .where(BBIE.BBIE_ID.in(
                        dslContext().select(BBIE.BBIE_ID)
                                .from(BBIE)
                                .join(TOP_LEVEL_ASBIEP)
                                .on(BBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID))
                                .where(and(TOP_LEVEL_ASBIEP.RELEASE_ID.eq(releaseId),
                                        BBIE.AGENCY_ID_LIST_ID.eq(prevAgencyIdListId)))))
                .execute();

        dslContext().update(BBIE_SC)
                .set(BBIE_SC.AGENCY_ID_LIST_ID, nextAgencyIdListId)
                .where(BBIE_SC.BBIE_SC_ID.in(
                        dslContext().select(BBIE_SC.BBIE_SC_ID)
                                .from(BBIE_SC)
                                .join(TOP_LEVEL_ASBIEP)
                                .on(BBIE_SC.OWNER_TOP_LEVEL_ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID))
                                .where(and(TOP_LEVEL_ASBIEP.RELEASE_ID.eq(releaseId),
                                        BBIE_SC.AGENCY_ID_LIST_ID.eq(prevAgencyIdListId)))))
                .execute();

    }

    private void discardLogAgencyIdListValues(AgencyIdListManifestRecord agencyIdListManifestRecord,
                                              AgencyIdListRecord agencyIdListRecord) {
        List<AgencyIdListValueManifestRecord> agencyIdListValueManifests = dslContext().selectFrom(AGENCY_ID_LIST_VALUE_MANIFEST)
                .where(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(agencyIdListManifestRecord.getAgencyIdListManifestId()))
                .fetch();

        for (AgencyIdListValueManifestRecord agencyIdListValueManifest : agencyIdListValueManifests) {
            AgencyIdListValueRecord agencyIdListValue = dslContext().selectFrom(AGENCY_ID_LIST_VALUE)
                    .where(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID.eq(agencyIdListValueManifest.getAgencyIdListValueId()))
                    .fetchOne();

            if (agencyIdListValue.getPrevAgencyIdListValueId() == null) {
                // delete code list value and code list manifest which added this revision
                agencyIdListValueManifest.delete();
                agencyIdListValue.delete();
            } else {
                // delete code list value and update code list value manifest
                AgencyIdListValueRecord prevAgencyIdListValue = dslContext().selectFrom(AGENCY_ID_LIST_VALUE)
                        .where(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID.eq(agencyIdListValue.getPrevAgencyIdListValueId()))
                        .fetchOne();

                updateCodeListAgencyIdListValueId(agencyIdListManifestRecord.getReleaseId(),
                        agencyIdListValue.getAgencyIdListValueId(),
                        prevAgencyIdListValue.getAgencyIdListValueId());

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
            String targetReleaseId,
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
            nextAgencyIdListValueRecord.setAgencyIdListValueId(UUID.randomUUID().toString());
            nextAgencyIdListValueRecord.setOwnerListId(nextAgencyIdListRecord.getAgencyIdListId());
            nextAgencyIdListValueRecord.setCreatedBy(user.getUserId());
            nextAgencyIdListValueRecord.setLastUpdatedBy(user.getUserId());
            nextAgencyIdListValueRecord.setOwnerUserId(user.getUserId());
            nextAgencyIdListValueRecord.setCreationTimestamp(timestamp);
            nextAgencyIdListValueRecord.setLastUpdateTimestamp(timestamp);
            nextAgencyIdListValueRecord.setPrevAgencyIdListValueId(prevAgencyIdListValueRecord.getAgencyIdListValueId());

            dslContext().insertInto(AGENCY_ID_LIST_VALUE)
                    .set(nextAgencyIdListValueRecord)
                    .execute();

            prevAgencyIdListValueRecord.setNextAgencyIdListValueId(nextAgencyIdListValueRecord.getAgencyIdListValueId());
            prevAgencyIdListValueRecord.update(AGENCY_ID_LIST_VALUE.NEXT_AGENCY_ID_LIST_VALUE_ID);

            agencyIdListValueManifestRecord.setAgencyIdListValueId(nextAgencyIdListValueRecord.getAgencyIdListValueId());
            agencyIdListValueManifestRecord.setAgencyIdListManifestId(manifestRecord.getAgencyIdListManifestId());
            agencyIdListValueManifestRecord.update(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_ID,
                    AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID);

            updateCodeListAgencyIdListValueId(manifestRecord.getReleaseId(),
                    prevAgencyIdListValueRecord.getAgencyIdListValueId(),
                    nextAgencyIdListValueRecord.getAgencyIdListValueId());
        }
    }
}
