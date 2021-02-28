package org.oagi.score.repo.api.impl.jooq.module;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleSetRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleSetReleaseRecord;
import org.oagi.score.repo.api.module.ModuleSetReleaseWriteRepository;
import org.oagi.score.repo.api.module.model.*;
import org.oagi.score.repo.api.security.AccessControl;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.inline;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.user.model.ScoreRole.DEVELOPER;
import static org.oagi.score.repo.api.user.model.ScoreRole.END_USER;

public class JooqModuleSetReleaseWriteRepository
        extends JooqScoreRepository
        implements ModuleSetReleaseWriteRepository {

    public JooqModuleSetReleaseWriteRepository(DSLContext dslContext) {
        super(dslContext);
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public CreateModuleSetReleaseResponse createModuleSetRelease(CreateModuleSetReleaseRequest request) throws ScoreDataAccessException {
        ScoreUser requester = request.getRequester();
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        ModuleSetReleaseRecord moduleSetReleaseRecord = dslContext().insertInto(MODULE_SET_RELEASE)
                .set(MODULE_SET_RELEASE.RELEASE_ID, ULong.valueOf(request.getReleaseId()))
                .set(MODULE_SET_RELEASE.MODULE_SET_ID, ULong.valueOf(request.getModuleSetId()))
                .set(MODULE_SET_RELEASE.IS_DEFAULT, request.isDefault() ? (byte) 1 : 0)
                .set(MODULE_SET_RELEASE.CREATED_BY, requesterUserId)
                .set(MODULE_SET_RELEASE.LAST_UPDATED_BY, requesterUserId)
                .set(MODULE_SET_RELEASE.CREATION_TIMESTAMP, timestamp)
                .set(MODULE_SET_RELEASE.LAST_UPDATE_TIMESTAMP, timestamp)
                .returning()
                .fetchOne();

        ModuleSetRelease moduleSetRelease = new ModuleSetRelease();
        moduleSetRelease.setModuleSetReleaseId(moduleSetReleaseRecord.getModuleSetReleaseId().toBigInteger());
        moduleSetRelease.setReleaseId(moduleSetReleaseRecord.getReleaseId().toBigInteger());
        moduleSetRelease.setModuleSetId(moduleSetReleaseRecord.getModuleSetId().toBigInteger());
        moduleSetRelease.setDefault(request.isDefault());
        moduleSetRelease.setCreatedBy(requester);
        moduleSetRelease.setCreationTimestamp(
                Date.from(moduleSetReleaseRecord.getCreationTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
        moduleSetRelease.setLastUpdatedBy(requester);
        moduleSetRelease.setLastUpdateTimestamp(
                Date.from(moduleSetReleaseRecord.getLastUpdateTimestamp().atZone(ZoneId.systemDefault()).toInstant()));

        copyModuleCcManifest(requesterUserId, timestamp, moduleSetReleaseRecord, ULong.valueOf(request.getBaseModuleSetReleaseId()));

        return new CreateModuleSetReleaseResponse(moduleSetRelease);
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public UpdateModuleSetReleaseResponse updateModuleSetRelease(UpdateModuleSetReleaseRequest request) throws ScoreDataAccessException {
        ScoreUser requester = request.getRequester();
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        ModuleSetReleaseRecord moduleSetReleaseRecord = dslContext().update(MODULE_SET_RELEASE)
                .set(MODULE_SET_RELEASE.RELEASE_ID, ULong.valueOf(request.getReleaseId()))
                .set(MODULE_SET_RELEASE.MODULE_SET_ID, ULong.valueOf(request.getModuleSetId()))
                .set(MODULE_SET_RELEASE.IS_DEFAULT, request.isDefault() ? (byte) 1 : 0)
                .set(MODULE_SET_RELEASE.LAST_UPDATED_BY, requesterUserId)
                .set(MODULE_SET_RELEASE.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(MODULE_SET_RELEASE.MODULE_SET_RELEASE_ID.eq(ULong.valueOf(request.getModuleSetReleaseId())))
                .returning().fetchOne();

        ModuleSetRelease moduleSetRelease = new ModuleSetRelease();
        moduleSetRelease.setModuleSetReleaseId(moduleSetReleaseRecord.getModuleSetReleaseId().toBigInteger());
        moduleSetRelease.setReleaseId(moduleSetReleaseRecord.getReleaseId().toBigInteger());
        moduleSetRelease.setModuleSetId(moduleSetReleaseRecord.getModuleSetId().toBigInteger());
        moduleSetRelease.setDefault(request.isDefault());

        moduleSetRelease.setCreationTimestamp(
                Date.from(moduleSetReleaseRecord.getCreationTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
        moduleSetRelease.setLastUpdatedBy(requester);
        moduleSetRelease.setLastUpdateTimestamp(
                Date.from(moduleSetReleaseRecord.getLastUpdateTimestamp().atZone(ZoneId.systemDefault()).toInstant()));

        return new UpdateModuleSetReleaseResponse(moduleSetRelease);
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public DeleteModuleSetReleaseResponse deleteModuleSetRelease(DeleteModuleSetReleaseRequest request) throws ScoreDataAccessException {
        dslContext().deleteFrom(MODULE_SET_RELEASE)
                .where(MODULE_SET_RELEASE.MODULE_SET_RELEASE_ID.eq(ULong.valueOf(request.getModuleSetReleaseId())))
                .execute();

        return new DeleteModuleSetReleaseResponse();
    }

    private void copyModuleCcManifest(ULong requesterUserId, LocalDateTime timestamp,
                                     ModuleSetReleaseRecord moduleSetReleaseRecord, ULong baseModuleSetReleaseId) {

        ULong releaseId = moduleSetReleaseRecord.getReleaseId();

        // copy MODULE_ACC_MANIFEST
        dslContext().insertInto(MODULE_ACC_MANIFEST,
                MODULE_ACC_MANIFEST.MODULE_SET_RELEASE_ID,
                MODULE_ACC_MANIFEST.ACC_MANIFEST_ID,
                MODULE_ACC_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                MODULE_ACC_MANIFEST.CREATED_BY,
                MODULE_ACC_MANIFEST.CREATION_TIMESTAMP,
                MODULE_ACC_MANIFEST.LAST_UPDATED_BY,
                MODULE_ACC_MANIFEST.LAST_UPDATE_TIMESTAMP)
                .select(dslContext().select(
                        inline(moduleSetReleaseRecord.getModuleSetReleaseId()),
                        ACC_MANIFEST.as("acc_manifest_target").ACC_MANIFEST_ID,
                        MODULE_ACC_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                        inline(requesterUserId),
                        inline(timestamp),
                        inline(requesterUserId),
                        inline(timestamp))
                        .from(MODULE_ACC_MANIFEST)
                        .join(ACC_MANIFEST).on(MODULE_ACC_MANIFEST.ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                        .join(ACC).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                        .join(ACC.as("acc_target")).on(ACC.GUID.eq(ACC.as("acc_target").GUID))
                        .join(ACC_MANIFEST.as("acc_manifest_target")).on(and(
                                ACC.as("acc_target").ACC_ID.eq(ACC_MANIFEST.as("acc_manifest_target").ACC_ID),
                                ACC_MANIFEST.as("acc_manifest_target").RELEASE_ID.eq(releaseId)))
                        .where(MODULE_ACC_MANIFEST.MODULE_SET_RELEASE_ID.eq(baseModuleSetReleaseId)))
                .execute();

        // copy MODULE_ASCCP_MANIFEST
        dslContext().insertInto(MODULE_ASCCP_MANIFEST,
                MODULE_ASCCP_MANIFEST.MODULE_SET_RELEASE_ID,
                MODULE_ASCCP_MANIFEST.ASCCP_MANIFEST_ID,
                MODULE_ASCCP_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                MODULE_ASCCP_MANIFEST.CREATED_BY,
                MODULE_ASCCP_MANIFEST.CREATION_TIMESTAMP,
                MODULE_ASCCP_MANIFEST.LAST_UPDATED_BY,
                MODULE_ASCCP_MANIFEST.LAST_UPDATE_TIMESTAMP)
                .select(dslContext().select(
                        inline(moduleSetReleaseRecord.getModuleSetReleaseId()),
                        ASCCP_MANIFEST.as("asccp_manifest_target").ASCCP_MANIFEST_ID,
                        MODULE_ASCCP_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                        inline(requesterUserId),
                        inline(timestamp),
                        inline(requesterUserId),
                        inline(timestamp))
                        .from(MODULE_ASCCP_MANIFEST)
                        .join(ASCCP_MANIFEST).on(MODULE_ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                        .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                        .join(ASCCP.as("asccp_target")).on(ASCCP.GUID.eq(ASCCP.as("asccp_target").GUID))
                        .join(ASCCP_MANIFEST.as("asccp_manifest_target")).on(and(
                                ASCCP.as("asccp_target").ASCCP_ID.eq(ASCCP_MANIFEST.as("asccp_manifest_target").ASCCP_ID),
                                ASCCP_MANIFEST.as("asccp_manifest_target").RELEASE_ID.eq(releaseId)))
                        .where(MODULE_ASCCP_MANIFEST.MODULE_SET_RELEASE_ID.eq(baseModuleSetReleaseId)))
                .execute();

        // copy MODULE_BCCP_MANIFEST
        dslContext().insertInto(MODULE_BCCP_MANIFEST,
                MODULE_BCCP_MANIFEST.MODULE_SET_RELEASE_ID,
                MODULE_BCCP_MANIFEST.BCCP_MANIFEST_ID,
                MODULE_BCCP_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                MODULE_BCCP_MANIFEST.CREATED_BY,
                MODULE_BCCP_MANIFEST.CREATION_TIMESTAMP,
                MODULE_BCCP_MANIFEST.LAST_UPDATED_BY,
                MODULE_BCCP_MANIFEST.LAST_UPDATE_TIMESTAMP)
                .select(dslContext().select(
                        inline(moduleSetReleaseRecord.getModuleSetReleaseId()),
                        BCCP_MANIFEST.as("bccp_manifest_target").BCCP_MANIFEST_ID,
                        MODULE_BCCP_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                        inline(requesterUserId),
                        inline(timestamp),
                        inline(requesterUserId),
                        inline(timestamp))
                        .from(MODULE_BCCP_MANIFEST)
                        .join(BCCP_MANIFEST).on(MODULE_BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(BCCP_MANIFEST.BCCP_MANIFEST_ID))
                        .join(BCCP).on(BCCP_MANIFEST.BCCP_ID.eq(BCCP.BCCP_ID))
                        .join(BCCP.as("bccp_target")).on(BCCP.GUID.eq(BCCP.as("bccp_target").GUID))
                        .join(BCCP_MANIFEST.as("bccp_manifest_target")).on(and(
                                BCCP.as("bccp_target").BCCP_ID.eq(BCCP_MANIFEST.as("bccp_manifest_target").BCCP_ID),
                                BCCP_MANIFEST.as("bccp_manifest_target").RELEASE_ID.eq(releaseId)))
                        .where(MODULE_BCCP_MANIFEST.MODULE_SET_RELEASE_ID.eq(baseModuleSetReleaseId)))
                .execute();

        // copy MODULE_CODE_LIST_MANIFEST
        dslContext().insertInto(MODULE_CODE_LIST_MANIFEST,
                MODULE_CODE_LIST_MANIFEST.MODULE_SET_RELEASE_ID,
                MODULE_CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID,
                MODULE_CODE_LIST_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                MODULE_CODE_LIST_MANIFEST.CREATED_BY,
                MODULE_CODE_LIST_MANIFEST.CREATION_TIMESTAMP,
                MODULE_CODE_LIST_MANIFEST.LAST_UPDATED_BY,
                MODULE_CODE_LIST_MANIFEST.LAST_UPDATE_TIMESTAMP)
                .select(dslContext().select(
                        inline(moduleSetReleaseRecord.getModuleSetReleaseId()),
                        CODE_LIST_MANIFEST.as("code_list_manifest_target").CODE_LIST_MANIFEST_ID,
                        MODULE_CODE_LIST_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                        inline(requesterUserId),
                        inline(timestamp),
                        inline(requesterUserId),
                        inline(timestamp))
                        .from(MODULE_CODE_LIST_MANIFEST)
                        .join(CODE_LIST_MANIFEST).on(MODULE_CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID))
                        .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                        .join(CODE_LIST.as("code_list_target")).on(CODE_LIST.GUID.eq(CODE_LIST.as("code_list_target").GUID))
                        .join(CODE_LIST_MANIFEST.as("code_list_manifest_target")).on(and(
                                CODE_LIST.as("code_list_target").CODE_LIST_ID.eq(CODE_LIST_MANIFEST.as("code_list_manifest_target").CODE_LIST_ID),
                                CODE_LIST_MANIFEST.as("code_list_manifest_target").RELEASE_ID.eq(releaseId)))
                        .where(MODULE_CODE_LIST_MANIFEST.MODULE_SET_RELEASE_ID.eq(baseModuleSetReleaseId)))
                .execute();

        // copy MODULE_AGENCY_ID_LIST_MANIFEST
        dslContext().insertInto(MODULE_AGENCY_ID_LIST_MANIFEST,
                MODULE_AGENCY_ID_LIST_MANIFEST.MODULE_SET_RELEASE_ID,
                MODULE_AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID,
                MODULE_AGENCY_ID_LIST_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                MODULE_AGENCY_ID_LIST_MANIFEST.CREATED_BY,
                MODULE_AGENCY_ID_LIST_MANIFEST.CREATION_TIMESTAMP,
                MODULE_AGENCY_ID_LIST_MANIFEST.LAST_UPDATED_BY,
                MODULE_AGENCY_ID_LIST_MANIFEST.LAST_UPDATE_TIMESTAMP)
                .select(dslContext().select(
                        inline(moduleSetReleaseRecord.getModuleSetReleaseId()),
                        AGENCY_ID_LIST_MANIFEST.as("agency_id_list_manifest_target").AGENCY_ID_LIST_MANIFEST_ID,
                        MODULE_AGENCY_ID_LIST_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                        inline(requesterUserId),
                        inline(timestamp),
                        inline(requesterUserId),
                        inline(timestamp))
                        .from(MODULE_AGENCY_ID_LIST_MANIFEST)
                        .join(AGENCY_ID_LIST_MANIFEST).on(MODULE_AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID))
                        .join(AGENCY_ID_LIST).on(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST.AGENCY_ID_LIST_ID))
                        .join(AGENCY_ID_LIST.as("agency_id_list_target")).on(AGENCY_ID_LIST.GUID.eq(AGENCY_ID_LIST.as("agency_id_list_target").GUID))
                        .join(AGENCY_ID_LIST_MANIFEST.as("agency_id_list_manifest_target")).on(and(
                                AGENCY_ID_LIST.as("agency_id_list_target").AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST_MANIFEST.as("agency_id_list_manifest_target").AGENCY_ID_LIST_ID),
                                AGENCY_ID_LIST_MANIFEST.as("agency_id_list_manifest_target").RELEASE_ID.eq(releaseId)))
                        .where(MODULE_AGENCY_ID_LIST_MANIFEST.MODULE_SET_RELEASE_ID.eq(baseModuleSetReleaseId)))
                .execute();

        // copy MODULE_BLOB_CONTENT_MANIFEST
        dslContext().insertInto(MODULE_BLOB_CONTENT_MANIFEST,
                MODULE_BLOB_CONTENT_MANIFEST.MODULE_SET_RELEASE_ID,
                MODULE_BLOB_CONTENT_MANIFEST.BLOB_CONTENT_MANIFEST_ID,
                MODULE_BLOB_CONTENT_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                MODULE_BLOB_CONTENT_MANIFEST.CREATED_BY,
                MODULE_BLOB_CONTENT_MANIFEST.CREATION_TIMESTAMP,
                MODULE_BLOB_CONTENT_MANIFEST.LAST_UPDATED_BY,
                MODULE_BLOB_CONTENT_MANIFEST.LAST_UPDATE_TIMESTAMP)
                .select(dslContext().select(
                        inline(moduleSetReleaseRecord.getModuleSetReleaseId()),
                        MODULE_BLOB_CONTENT_MANIFEST.BLOB_CONTENT_MANIFEST_ID,
                        MODULE_BLOB_CONTENT_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                        inline(requesterUserId),
                        inline(timestamp),
                        inline(requesterUserId),
                        inline(timestamp))
                        .from(MODULE_BLOB_CONTENT_MANIFEST)
                        .join(BLOB_CONTENT_MANIFEST).on(MODULE_BLOB_CONTENT_MANIFEST.BLOB_CONTENT_MANIFEST_ID.eq(BLOB_CONTENT_MANIFEST.BLOB_CONTENT_MANIFEST_ID))
                        .where(MODULE_BLOB_CONTENT_MANIFEST.MODULE_SET_RELEASE_ID.eq(baseModuleSetReleaseId)))
                .execute();

        // copy MODULE_XBT_MANIFEST
        dslContext().insertInto(MODULE_XBT_MANIFEST,
                MODULE_XBT_MANIFEST.MODULE_SET_RELEASE_ID,
                MODULE_XBT_MANIFEST.XBT_MANIFEST_ID,
                MODULE_XBT_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                MODULE_XBT_MANIFEST.CREATED_BY,
                MODULE_XBT_MANIFEST.CREATION_TIMESTAMP,
                MODULE_XBT_MANIFEST.LAST_UPDATED_BY,
                MODULE_XBT_MANIFEST.LAST_UPDATE_TIMESTAMP)
                .select(dslContext().select(
                        inline(moduleSetReleaseRecord.getModuleSetReleaseId()),
                        XBT_MANIFEST.as("xbt_manifest_target").XBT_MANIFEST_ID,
                        MODULE_XBT_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                        inline(requesterUserId),
                        inline(timestamp),
                        inline(requesterUserId),
                        inline(timestamp))
                        .from(MODULE_XBT_MANIFEST)
                        .join(XBT_MANIFEST).on(MODULE_XBT_MANIFEST.XBT_MANIFEST_ID.eq(XBT_MANIFEST.XBT_MANIFEST_ID))
                        .join(XBT).on(XBT_MANIFEST.XBT_ID.eq(XBT.XBT_ID))
                        .join(XBT.as("xbt_target")).on(XBT.GUID.eq(XBT.as("xbt_target").GUID))
                        .join(XBT_MANIFEST.as("xbt_manifest_target")).on(and(
                                XBT.as("xbt_target").XBT_ID.eq(XBT_MANIFEST.as("xbt_manifest_target").XBT_ID),
                                XBT_MANIFEST.as("xbt_manifest_target").RELEASE_ID.eq(releaseId)))
                        .where(MODULE_XBT_MANIFEST.MODULE_SET_RELEASE_ID.eq(baseModuleSetReleaseId)))
                .execute();

        // copy MODULE_DT_MANIFEST
        dslContext().insertInto(MODULE_DT_MANIFEST,
                MODULE_DT_MANIFEST.MODULE_SET_RELEASE_ID,
                MODULE_DT_MANIFEST.DT_MANIFEST_ID,
                MODULE_DT_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                MODULE_DT_MANIFEST.CREATED_BY,
                MODULE_DT_MANIFEST.CREATION_TIMESTAMP,
                MODULE_DT_MANIFEST.LAST_UPDATED_BY,
                MODULE_DT_MANIFEST.LAST_UPDATE_TIMESTAMP)
                .select(dslContext().select(
                        inline(moduleSetReleaseRecord.getModuleSetReleaseId()),
                        DT_MANIFEST.as("dt_manifest_target").DT_MANIFEST_ID,
                        MODULE_DT_MANIFEST.MODULE_SET_ASSIGNMENT_ID,
                        inline(requesterUserId),
                        inline(timestamp),
                        inline(requesterUserId),
                        inline(timestamp))
                        .from(MODULE_DT_MANIFEST)
                        .join(DT_MANIFEST).on(MODULE_DT_MANIFEST.DT_MANIFEST_ID.eq(DT_MANIFEST.DT_MANIFEST_ID))
                        .join(DT).on(DT_MANIFEST.DT_ID.eq(DT.DT_ID))
                        .join(DT.as("dt_target")).on(DT.GUID.eq(DT.as("dt_target").GUID))
                        .join(DT_MANIFEST.as("dt_manifest_target")).on(and(
                                DT.as("dt_target").DT_ID.eq(DT_MANIFEST.as("dt_manifest_target").DT_ID),
                                DT_MANIFEST.as("dt_manifest_target").RELEASE_ID.eq(releaseId)))
                        .where(MODULE_DT_MANIFEST.MODULE_SET_RELEASE_ID.eq(baseModuleSetReleaseId)))
                .execute();

    }
}