package org.oagi.score.repo.api.impl.jooq.module;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleDirRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleSetRecord;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.repo.api.module.ModuleWriteRepository;
import org.oagi.score.repo.api.module.model.*;
import org.oagi.score.repo.api.module.model.Module;
import org.oagi.score.repo.api.security.AccessControl;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.user.model.ScoreRole.DEVELOPER;
import static org.oagi.score.repo.api.user.model.ScoreRole.END_USER;

public class JooqModuleWriteRepository
        extends JooqScoreRepository
        implements ModuleWriteRepository {

    public JooqModuleWriteRepository(DSLContext dslContext) {
        super(dslContext);
    }

    private final String MODULE_PATH_SEPARATOR = "\\";

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public CreateModuleResponse createModule(CreateModuleRequest request) throws ScoreDataAccessException {
        ScoreUser requester = request.getRequester();
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        ModuleRecord moduleRecord = dslContext().insertInto(MODULE)
                .set(MODULE.MODULE_DIR_ID, ULong.valueOf(request.getModuleDirId()))
                .set(MODULE.NAME, request.getName())
                .set(MODULE.NAMESPACE_ID, ULong.valueOf(request.getNamespaceId()))
                .set(MODULE.VERSION_NUM, request.getVersionNum())
                .set(MODULE.CREATED_BY, requesterUserId)
                .set(MODULE.OWNER_USER_ID, requesterUserId)
                .set(MODULE.LAST_UPDATED_BY, requesterUserId)
                .set(MODULE.CREATION_TIMESTAMP, timestamp)
                .set(MODULE.LAST_UPDATE_TIMESTAMP, timestamp)
                .returning()
                .fetchOne();

        Module module = new Module();
        module.setModuleId(moduleRecord.getModuleId().toBigInteger());
        module.setModuleDirId(moduleRecord.getModuleDirId().toBigInteger());
        module.setName(moduleRecord.getName());
        module.setVersionNum(moduleRecord.getVersionNum());
        module.setCreatedBy(requester);
        module.setCreationTimestamp(
                Date.from(moduleRecord.getCreationTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
        module.setLastUpdatedBy(requester);
        module.setLastUpdateTimestamp(
                Date.from(moduleRecord.getLastUpdateTimestamp().atZone(ZoneId.systemDefault()).toInstant()));

        if (request.getModuleSetId() != null) {
            dslContext().insertInto(MODULE_SET_ASSIGNMENT)
                    .set(MODULE_SET_ASSIGNMENT.MODULE_ID, moduleRecord.getModuleId())
                    .set(MODULE_SET_ASSIGNMENT.MODULE_SET_ID, ULong.valueOf(request.getModuleSetId()))
                    .set(MODULE_SET_ASSIGNMENT.CREATED_BY, requesterUserId)
                    .set(MODULE_SET_ASSIGNMENT.LAST_UPDATED_BY, requesterUserId)
                    .set(MODULE_SET_ASSIGNMENT.CREATION_TIMESTAMP, timestamp)
                    .set(MODULE_SET_ASSIGNMENT.LAST_UPDATE_TIMESTAMP, timestamp)
                    .execute();
        }

        return new CreateModuleResponse(module);
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public UpdateModuleResponse updateModule(UpdateModuleRequest request) throws ScoreDataAccessException {
        ScoreUser requester = request.getRequester();
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        ModuleRecord moduleRecord = dslContext().selectFrom(MODULE)
                .where(MODULE.MODULE_ID.eq(ULong.valueOf(request.getModuleId())))
                .fetchOne();

        if (moduleRecord == null) {
            throw new IllegalArgumentException("Can not found Module");
        }

        if (StringUtils.hasLength(request.getName())) {
            moduleRecord.setName(request.getName());
        }

        if (request.getNamespaceId() != null) {
            moduleRecord.setNamespaceId(ULong.valueOf(request.getNamespaceId()));
        }

        if (StringUtils.hasLength(request.getVersionNum())) {
            moduleRecord.setVersionNum(request.getVersionNum());
        }

        if (moduleRecord.changed()) {
            moduleRecord.setLastUpdatedBy(requesterUserId);
            moduleRecord.setLastUpdateTimestamp(timestamp);
            moduleRecord.update();
        }

        Module module = new Module();
        module.setModuleId(moduleRecord.getModuleId().toBigInteger());
        module.setModuleDirId(moduleRecord.getModuleDirId().toBigInteger());
        module.setName(moduleRecord.getName());
        module.setVersionNum(moduleRecord.getVersionNum());

        module.setCreationTimestamp(
                Date.from(moduleRecord.getCreationTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
        module.setLastUpdatedBy(requester);
        module.setLastUpdateTimestamp(
                Date.from(moduleRecord.getLastUpdateTimestamp().atZone(ZoneId.systemDefault()).toInstant()));

        return new UpdateModuleResponse(module);
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public DeleteModuleResponse deleteModule(DeleteModuleRequest request) throws ScoreDataAccessException {
        dslContext().deleteFrom(MODULE)
                .where(MODULE.MODULE_ID.eq(ULong.valueOf(request.getModuleId())));

        return new DeleteModuleResponse();
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public CreateModuleDirResponse createModuleDir(CreateModuleDirRequest request) throws ScoreDataAccessException {
        ScoreUser requester = request.getRequester();
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        ModuleDirRecord parent = dslContext().selectFrom(MODULE_DIR)
                .where(MODULE_DIR.MODULE_DIR_ID.eq(ULong.valueOf(request.getParentModuleDirId())))
                .fetchOne();

        if (parent == null) {
            throw new IllegalArgumentException("Can not found parent ModuleDir");
        }

        ModuleDirRecord moduleDirRecord = dslContext().insertInto(MODULE_DIR)
                .set(MODULE_DIR.PARENT_MODULE_DIR_ID, parent.getModuleDirId())
                .set(MODULE_DIR.NAME, request.getName())
                .set(MODULE_DIR.PATH, parent.getPath() + MODULE_PATH_SEPARATOR + request.getName())
                .set(MODULE_DIR.CREATED_BY, requesterUserId)
                .set(MODULE_DIR.LAST_UPDATED_BY, requesterUserId)
                .set(MODULE_DIR.CREATION_TIMESTAMP, timestamp)
                .set(MODULE_DIR.LAST_UPDATE_TIMESTAMP, timestamp)
                .returning()
                .fetchOne();

        ModuleDir moduleDir = new ModuleDir();
        moduleDir.setModuleDirId(moduleDirRecord.getModuleDirId().toBigInteger());
        moduleDir.setParentModuleDirId(moduleDirRecord.getParentModuleDirId().toBigInteger());
        moduleDir.setName(moduleDirRecord.getName());
        moduleDir.setPath(moduleDirRecord.getPath());
        moduleDir.setCreatedBy(requester);
        moduleDir.setCreationTimestamp(
                Date.from(moduleDirRecord.getCreationTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
        moduleDir.setLastUpdatedBy(requester);
        moduleDir.setLastUpdateTimestamp(
                Date.from(moduleDirRecord.getLastUpdateTimestamp().atZone(ZoneId.systemDefault()).toInstant()));

        return new CreateModuleDirResponse(moduleDir);
    }

    @Override
    public UpdateModuleDirResponse updateModuleDir(UpdateModuleDirRequest request) throws ScoreDataAccessException {
        ScoreUser requester = request.getRequester();
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        ModuleDirRecord moduleDirRecord = dslContext().selectFrom(MODULE_DIR)
                .where(MODULE_DIR.MODULE_DIR_ID.eq(ULong.valueOf(request.getModuleDirId())))
                .fetchOne();

        if (moduleDirRecord == null) {
            throw new IllegalArgumentException("Can not found ModuleDir");
        }

        if (StringUtils.hasLength(request.getName())) {
            moduleDirRecord.setName(request.getName());
        }

        if (moduleDirRecord.changed()) {
            moduleDirRecord.setLastUpdatedBy(requesterUserId);
            moduleDirRecord.setLastUpdateTimestamp(timestamp);
            moduleDirRecord.update();
        }

        ModuleDir moduleDir = new ModuleDir();
        moduleDir.setModuleDirId(moduleDirRecord.getModuleDirId().toBigInteger());
        moduleDir.setParentModuleDirId(moduleDirRecord.getParentModuleDirId().toBigInteger());
        moduleDir.setName(moduleDirRecord.getName());
        moduleDir.setPath(moduleDirRecord.getPath());
        moduleDir.setCreatedBy(requester);
        moduleDir.setCreationTimestamp(
                Date.from(moduleDirRecord.getCreationTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
        moduleDir.setLastUpdatedBy(requester);
        moduleDir.setLastUpdateTimestamp(
                Date.from(moduleDirRecord.getLastUpdateTimestamp().atZone(ZoneId.systemDefault()).toInstant()));

        return new UpdateModuleDirResponse(moduleDir);
    }

    @Override
    public DeleteModuleDirResponse deleteModuleDir(DeleteModuleDirRequest request) throws ScoreDataAccessException {
        return null;
    }

    @Override
    public void copyModuleDir(CopyModuleDirRequest request) throws ScoreDataAccessException {
        ScoreUser requester = request.getRequester();
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        ModuleSetRecord moduleSetRecord = dslContext().selectFrom(MODULE_SET)
                .where(MODULE_SET.MODULE_SET_ID.eq(ULong.valueOf(request.getModuleSetId()))).fetchOne();

        if (moduleSetRecord == null) {
            throw new IllegalArgumentException("Can not found ModuleSet");
        }

        ModuleDirRecord moduleDirRecord = dslContext().selectFrom(MODULE_DIR)
                .where(MODULE_DIR.MODULE_DIR_ID.eq(ULong.valueOf(request.getModuleDirId()))).fetchOne();

        if (moduleDirRecord == null) {
            throw new IllegalArgumentException("Can not found ModuleDir");
        }

        copyModuleDir(requesterUserId, moduleDirRecord, ULong.valueOf(request.getCopyPosDirId()), moduleSetRecord, timestamp);
    }

    private void copyModuleDir(ULong requesterId, ModuleDirRecord moduleDirRecord, ULong parentModuleDirId,
                               ModuleSetRecord moduleSetRecord, LocalDateTime timestamp) {
        ULong insertedModuleDirId = dslContext().insertInto(MODULE_DIR)
                .set(MODULE_DIR.PARENT_MODULE_DIR_ID, parentModuleDirId)
                .set(MODULE_DIR.NAME, moduleDirRecord.getName())
                .set(MODULE_DIR.PATH, moduleDirRecord.getPath())
                .set(MODULE_DIR.CREATED_BY, requesterId)
                .set(MODULE_DIR.LAST_UPDATED_BY, requesterId)
                .set(MODULE_DIR.CREATION_TIMESTAMP, timestamp)
                .set(MODULE_DIR.LAST_UPDATE_TIMESTAMP, timestamp)
                .returning().fetchOne().getModuleDirId();

        copyModuleAndAssign(requesterId, moduleDirRecord.getModuleDirId() , insertedModuleDirId, moduleSetRecord.getModuleSetId(), timestamp);

        getModuleDirByParentModuleDirId(moduleDirRecord.getModuleDirId()).forEach(dir -> {
            copyModuleDir(requesterId, dir, insertedModuleDirId, moduleSetRecord, timestamp);
        });
    }

    @Override
    public void copyModule(CopyModuleRequest request) throws ScoreDataAccessException {

        ScoreUser requester = request.getRequester();
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        ModuleRecord moduleRecord = dslContext().selectFrom(MODULE)
                .where(MODULE.MODULE_ID.eq(ULong.valueOf(request.getModuleId()))).fetchOne();

        ULong insertedModuleId = dslContext().insertInto(MODULE)
                .set(MODULE.MODULE_DIR_ID, ULong.valueOf(request.getCopyPosDirId()))
                .set(MODULE.NAME, moduleRecord.getName())
                .set(MODULE.NAMESPACE_ID, moduleRecord.getNamespaceId())
                .set(MODULE.VERSION_NUM, moduleRecord.getVersionNum())
                .set(MODULE.CREATED_BY, requesterUserId)
                .set(MODULE.OWNER_USER_ID, requesterUserId)
                .set(MODULE.LAST_UPDATED_BY, requesterUserId)
                .set(MODULE.CREATION_TIMESTAMP, timestamp)
                .set(MODULE.LAST_UPDATE_TIMESTAMP, timestamp)
                .returning().fetchOne().getModuleId();

        dslContext().insertInto(MODULE_SET_ASSIGNMENT)
                .set(MODULE_SET_ASSIGNMENT.MODULE_ID, insertedModuleId)
                .set(MODULE_SET_ASSIGNMENT.MODULE_SET_ID, ULong.valueOf(request.getModuleSetId()))
                .set(MODULE_SET_ASSIGNMENT.CREATED_BY, requesterUserId)
                .set(MODULE_SET_ASSIGNMENT.LAST_UPDATED_BY, requesterUserId)
                .set(MODULE_SET_ASSIGNMENT.CREATION_TIMESTAMP, timestamp)
                .set(MODULE_SET_ASSIGNMENT.LAST_UPDATE_TIMESTAMP, timestamp)
                .execute();
    }

    private void copyModuleAndAssign(ULong requesterId, ULong prevModuleDirId, ULong nextModuleDirId, ULong moduleSetId, LocalDateTime timestamp) {
        getModuleByModuleDir(prevModuleDirId).forEach(m -> {
            ULong insertedModuleId = dslContext().insertInto(MODULE)
                    .set(MODULE.MODULE_DIR_ID, nextModuleDirId)
                    .set(MODULE.NAME, m.getName())
                    .set(MODULE.NAMESPACE_ID, m.getNamespaceId())
                    .set(MODULE.VERSION_NUM, m.getVersionNum())
                    .set(MODULE.CREATED_BY, requesterId)
                    .set(MODULE.OWNER_USER_ID, requesterId)
                    .set(MODULE.LAST_UPDATED_BY, requesterId)
                    .set(MODULE.CREATION_TIMESTAMP, timestamp)
                    .set(MODULE.LAST_UPDATE_TIMESTAMP, timestamp)
                    .returning().fetchOne().getModuleId();

            dslContext().insertInto(MODULE_SET_ASSIGNMENT)
                    .set(MODULE_SET_ASSIGNMENT.MODULE_ID, insertedModuleId)
                    .set(MODULE_SET_ASSIGNMENT.MODULE_SET_ID, moduleSetId)
                    .set(MODULE_SET_ASSIGNMENT.CREATED_BY, requesterId)
                    .set(MODULE_SET_ASSIGNMENT.LAST_UPDATED_BY, requesterId)
                    .set(MODULE_SET_ASSIGNMENT.CREATION_TIMESTAMP, timestamp)
                    .set(MODULE_SET_ASSIGNMENT.LAST_UPDATE_TIMESTAMP, timestamp)
                    .execute();
        });
    }

    private List<ModuleDirRecord> getModuleDirByParentModuleDirId(ULong parentModuleDirId) {
        return dslContext().selectFrom(MODULE_DIR).where(MODULE_DIR.PARENT_MODULE_DIR_ID.eq(parentModuleDirId)).fetch();
    }

    private List<ModuleRecord> getModuleByModuleDir(ULong moduleDirId) {
        return dslContext().selectFrom(MODULE).where(MODULE.MODULE_DIR_ID.eq(moduleDirId)).fetch();
    }
}
