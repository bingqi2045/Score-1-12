package org.oagi.score.repo.api.impl.jooq.module;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleDirRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleSetAssignmentRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleSetRecord;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.repo.api.module.ModuleSetWriteRepository;
import org.oagi.score.repo.api.module.model.ModuleSet;
import org.oagi.score.repo.api.module.model.*;
import org.oagi.score.repo.api.security.AccessControl;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.impl.jooq.utils.ScoreGuidUtils.randomGuid;
import static org.oagi.score.repo.api.user.model.ScoreRole.DEVELOPER;
import static org.oagi.score.repo.api.user.model.ScoreRole.END_USER;

public class JooqModuleSetWriteRepository
        extends JooqScoreRepository
        implements ModuleSetWriteRepository {

    public JooqModuleSetWriteRepository(DSLContext dslContext) {
        super(dslContext);
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public CreateModuleSetResponse createModuleSet(CreateModuleSetRequest request) throws ScoreDataAccessException {
        ScoreUser requester = request.getRequester();
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        if (!StringUtils.hasLength(request.getName())) {
            throw new IllegalArgumentException("Module set name cannot be empty.");
        }

        ModuleSetRecord moduleSetRecord = dslContext().insertInto(MODULE_SET)
                .set(MODULE_SET.GUID, randomGuid())
                .set(MODULE_SET.NAME, request.getName())
                .set(MODULE_SET.DESCRIPTION, request.getDescription())
                .set(MODULE_SET.CREATED_BY, requesterUserId)
                .set(MODULE_SET.LAST_UPDATED_BY, requesterUserId)
                .set(MODULE_SET.CREATION_TIMESTAMP, timestamp)
                .set(MODULE_SET.LAST_UPDATE_TIMESTAMP, timestamp)
                .returning()
                .fetchOne();

        ModuleSet moduleSet = new ModuleSet();
        moduleSet.setModuleSetId(moduleSetRecord.getModuleSetId().toBigInteger());
        moduleSet.setName(moduleSetRecord.getName());
        moduleSet.setDescription(moduleSetRecord.getDescription());
        moduleSet.setCreatedBy(requester);
        moduleSet.setCreationTimestamp(
                Date.from(moduleSetRecord.getCreationTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
        moduleSet.setLastUpdatedBy(requester);
        moduleSet.setLastUpdateTimestamp(
                Date.from(moduleSetRecord.getLastUpdateTimestamp().atZone(ZoneId.systemDefault()).toInstant()));

        return new CreateModuleSetResponse(moduleSet);
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public UpdateModuleSetResponse updateModuleSet(UpdateModuleSetRequest request) throws ScoreDataAccessException {
        ScoreUser requester = request.getRequester();
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        if (!StringUtils.hasLength(request.getName())) {
            throw new IllegalArgumentException("Module set name cannot be empty.");
        }

        ModuleSetRecord moduleSetRecord = dslContext().selectFrom(MODULE_SET)
                .where(MODULE_SET.MODULE_SET_ID.eq(ULong.valueOf(request.getModuleSetId())))
                .fetchOne();

        if (moduleSetRecord == null) {
            throw new IllegalArgumentException("Can not found ModuleSet.");
        }

        moduleSetRecord.setName(request.getName());
        moduleSetRecord.setDescription(request.getDescription());

        if (moduleSetRecord.changed()) {
            moduleSetRecord.setLastUpdatedBy(requesterUserId);
            moduleSetRecord.setLastUpdateTimestamp(timestamp);
            moduleSetRecord.update();
        }

        ModuleSet moduleSet = new ModuleSet();
        moduleSet.setModuleSetId(moduleSetRecord.getModuleSetId().toBigInteger());
        moduleSet.setName(moduleSetRecord.getName());
        moduleSet.setDescription(moduleSetRecord.getDescription());

        moduleSet.setCreationTimestamp(
                Date.from(moduleSetRecord.getCreationTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
        moduleSet.setLastUpdatedBy(requester);
        moduleSet.setLastUpdateTimestamp(
                Date.from(moduleSetRecord.getLastUpdateTimestamp().atZone(ZoneId.systemDefault()).toInstant()));

        return new UpdateModuleSetResponse(moduleSet);
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public DeleteModuleSetResponse deleteModuleSet(DeleteModuleSetRequest request) throws ScoreDataAccessException {
        if (dslContext().selectFrom(MODULE_SET_RELEASE)
                .where(MODULE_SET_RELEASE.MODULE_SET_ID.eq(ULong.valueOf(request.getModuleSetId())))
                .fetch().size() > 0) {
            throw new IllegalArgumentException("This ModuleSet in use can not be discard.");
        }
        if (dslContext().selectFrom(MODULE_SET_ASSIGNMENT)
                .where(MODULE_SET_ASSIGNMENT.MODULE_SET_ID.eq(ULong.valueOf(request.getModuleSetId())))
                .fetch().size() > 0) {
            throw new IllegalArgumentException("This ModuleSet in use can not be discard.");
        }
        dslContext().deleteFrom(MODULE_SET)
                .where(MODULE_SET.MODULE_SET_ID.eq(ULong.valueOf(request.getModuleSetId()))).execute();

        return new DeleteModuleSetResponse();
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public DeleteModuleSetAssignmentResponse unassignModule(DeleteModuleSetAssignmentRequest request) throws ScoreDataAccessException {
        if (request.getModuleDirId() != null) {
            deleteModuleSetAssignmentByModuleDir(ULong.valueOf(request.getModuleSetId()), ULong.valueOf(request.getModuleDirId()));
        } else {
            deleteModuleSetAssignmentByModule(ULong.valueOf(request.getModuleSetId()), ULong.valueOf(request.getModuleId()));
        }

        return new DeleteModuleSetAssignmentResponse();
    }

    private void deleteModuleSetAssignmentByModule(ULong moduleSetId, ULong moduleId) {
        ModuleSetAssignmentRecord moduleSetAssignmentRecord = dslContext().selectFrom(MODULE_SET_ASSIGNMENT)
                .where(and(MODULE_SET_ASSIGNMENT.MODULE_ID.eq(moduleId),
                        MODULE_SET_ASSIGNMENT.MODULE_SET_ID.eq(moduleSetId)))
                .fetchOne();
        if (moduleSetAssignmentRecord == null) {
            throw new IllegalArgumentException("Cannot found assignment.");
        }
        try {
            moduleSetAssignmentRecord.delete();
        } catch (Exception e) {
            throw new IllegalArgumentException("This module has assigned core component, unassign core component first.");
        }
    }

    private void deleteModuleSetAssignmentByModuleDir(ULong moduleSetId, ULong moduleDirId) {
        dslContext().selectFrom(MODULE_DIR)
                .where(MODULE_DIR.PARENT_MODULE_DIR_ID.eq(moduleDirId))
                .fetch().forEach(e -> {
                    deleteModuleSetAssignmentByModuleDir(moduleSetId, e.getModuleDirId());
        });

        List<ULong> moduleSetAssignmentIdList = dslContext()
                .select(MODULE_SET_ASSIGNMENT.MODULE_SET_ASSIGNMENT_ID)
                .from(MODULE)
                .join(MODULE_SET_ASSIGNMENT).on(
                        and(MODULE.MODULE_ID.eq(MODULE_SET_ASSIGNMENT.MODULE_ID),
                                MODULE_SET_ASSIGNMENT.MODULE_SET_ID.eq(moduleSetId)))

                .where(MODULE.MODULE_DIR_ID.eq(moduleDirId))
                .fetchInto(ULong.class);

        try {
            dslContext().deleteFrom(MODULE_SET_ASSIGNMENT)
                    .where(MODULE_SET_ASSIGNMENT.MODULE_SET_ASSIGNMENT_ID.in(moduleSetAssignmentIdList)).execute();
        } catch (Exception e) {
            throw new IllegalArgumentException("This module has assigned core component, unassign core component first.");
        }
    }
}