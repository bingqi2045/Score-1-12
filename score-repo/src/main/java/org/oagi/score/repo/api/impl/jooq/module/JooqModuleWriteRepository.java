package org.oagi.score.repo.api.impl.jooq.module;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleDirRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleSetRecord;
import org.oagi.score.repo.api.impl.jooq.utils.ScoreGuidUtils;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.repo.api.module.ModuleWriteRepository;
import org.oagi.score.repo.api.module.model.*;
import org.oagi.score.repo.api.module.model.Module;
import org.oagi.score.repo.api.security.AccessControl;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.jooq.impl.DSL.*;
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

        if (hasDuplicateName(request.getModuleDirId(), request.getName())) {
            throw new IllegalArgumentException("Duplicate module name exist.");
        }

        // If a `namespaceId` parameter does not define in the request, overrides it by releases' one.
        if (request.getNamespaceId() == null && request.getModuleSetId() != null) {
            BigInteger releaseNamespaceId = dslContext().selectDistinct(RELEASE.NAMESPACE_ID)
                    .from(RELEASE)
                    .join(MODULE_SET_RELEASE).on(RELEASE.RELEASE_ID.eq(MODULE_SET_RELEASE.RELEASE_ID))
                    .where(and(
                            MODULE_SET_RELEASE.MODULE_SET_ID.eq(ULong.valueOf(request.getModuleSetId())),
                            MODULE_SET_RELEASE.IS_DEFAULT.eq((byte) 1)
                    ))
                    .fetchOneInto(BigInteger.class);
            request.setNamespaceId(releaseNamespaceId);
        }

        ModuleRecord moduleRecord = dslContext().insertInto(MODULE)
                .set(MODULE.MODULE_DIR_ID, ULong.valueOf(request.getModuleDirId()))
                .set(MODULE.NAME, request.getName())
                .set(MODULE.MODULE_SET_ID, ULong.valueOf(request.getModuleSetId()))
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
        module.setNamespaceId(moduleRecord.getNamespaceId().toBigInteger());
        module.setCreatedBy(requester);
        module.setCreationTimestamp(
                Date.from(moduleRecord.getCreationTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
        module.setLastUpdatedBy(requester);
        module.setLastUpdateTimestamp(
                Date.from(moduleRecord.getLastUpdateTimestamp().atZone(ZoneId.systemDefault()).toInstant()));

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

        if (StringUtils.hasLength(request.getName()) && !moduleRecord.getName().equals(request.getName())) {
            if (dslContext().selectFrom(MODULE_DIR)
                    .where(and(MODULE_DIR.PARENT_MODULE_DIR_ID.eq(moduleRecord.getModuleDirId()),
                            MODULE_DIR.NAME.eq(request.getName()))).fetch().size() > 0) {
                throw new IllegalArgumentException("Duplicate module name exist.");
            }

            if (dslContext().selectFrom(MODULE)
                    .where(and(MODULE.MODULE_DIR_ID.eq(moduleRecord.getModuleDirId()),
                            MODULE.MODULE_ID.notEqual(moduleRecord.getModuleId()),
                            MODULE.NAME.eq(request.getName()))).fetch().size() > 0) {
                throw new IllegalArgumentException("Duplicate module name exist.");
            }
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
        ULong moduleId = ULong.valueOf(request.getModuleId());
        deleteModule(moduleId);
        return new DeleteModuleResponse();
    }

    private void deleteModule(ULong moduleId) {
        dslContext().delete(MODULE_ACC_MANIFEST).where(MODULE_ACC_MANIFEST.MODULE_ID.eq(moduleId)).execute();
        dslContext().delete(MODULE_ASCCP_MANIFEST).where(MODULE_ASCCP_MANIFEST.MODULE_ID.eq(moduleId)).execute();
        dslContext().delete(MODULE_BCCP_MANIFEST).where(MODULE_BCCP_MANIFEST.MODULE_ID.eq(moduleId)).execute();
        dslContext().delete(MODULE_CODE_LIST_MANIFEST).where(MODULE_CODE_LIST_MANIFEST.MODULE_ID.eq(moduleId)).execute();
        dslContext().delete(MODULE_AGENCY_ID_LIST_MANIFEST).where(MODULE_AGENCY_ID_LIST_MANIFEST.MODULE_ID.eq(moduleId)).execute();
        dslContext().delete(MODULE_DT_MANIFEST).where(MODULE_DT_MANIFEST.MODULE_ID.eq(moduleId)).execute();
        dslContext().delete(MODULE_BLOB_CONTENT_MANIFEST).where(MODULE_BLOB_CONTENT_MANIFEST.MODULE_ID.eq(moduleId)).execute();
        dslContext().delete(MODULE_XBT_MANIFEST).where(MODULE_XBT_MANIFEST.MODULE_ID.eq(moduleId)).execute();

        dslContext().delete(MODULE).where(MODULE.MODULE_ID.eq(moduleId)).execute();
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

        if (hasDuplicateName(request.getParentModuleDirId(), request.getName())) {
            throw new IllegalArgumentException("Duplicate module name exist.");
        }

        ModuleDirRecord moduleDirRecord = dslContext().insertInto(MODULE_DIR)
                .set(MODULE_DIR.PARENT_MODULE_DIR_ID, parent.getModuleDirId())
                .set(MODULE_DIR.NAME, request.getName())
                .set(MODULE_DIR.MODULE_SET_ID, ULong.valueOf(request.getModuleSetId()))
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

        if (StringUtils.hasLength(request.getName()) && !moduleDirRecord.getName().equals(request.getName())) {
            if (dslContext().selectFrom(MODULE_DIR)
                    .where(and(MODULE_DIR.PARENT_MODULE_DIR_ID.eq(moduleDirRecord.getParentModuleDirId()),
                            MODULE_DIR.MODULE_DIR_ID.notEqual(moduleDirRecord.getModuleDirId()),
                            MODULE_DIR.NAME.eq(request.getName()))).fetch().size() > 0) {
                throw new IllegalArgumentException("Duplicate module name exist.");
            }

            if (dslContext().selectFrom(MODULE)
                    .where(and(MODULE.MODULE_DIR_ID.eq(moduleDirRecord.getParentModuleDirId()),
                            MODULE.NAME.eq(request.getName()))).fetch().size() > 0) {
                throw new IllegalArgumentException("Duplicate module name exist.");
            }

            List<String> tokens = Arrays.asList(moduleDirRecord.getPath().split(MODULE_PATH_SEPARATOR + MODULE_PATH_SEPARATOR));
            tokens.set(tokens.size() - 1, request.getName());

            moduleDirRecord.setName(request.getName());
            moduleDirRecord.setPath(String.join(MODULE_PATH_SEPARATOR, tokens));
            moduleDirRecord.setLastUpdatedBy(requesterUserId);
            moduleDirRecord.setLastUpdateTimestamp(timestamp);
            moduleDirRecord.update();
            broadcastModulePath(moduleDirRecord.getModuleDirId(), tokens);
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

    private void broadcastModulePath(ULong parentModuleDirId, List<String> tokens) {
        List<ModuleDirRecord> moduleDirRecordList = dslContext().selectFrom(MODULE_DIR)
                .where(MODULE_DIR.PARENT_MODULE_DIR_ID.eq(parentModuleDirId))
                .fetch();

        moduleDirRecordList.forEach(e -> {
            e.setPath(String.join(MODULE_PATH_SEPARATOR, tokens) + MODULE_PATH_SEPARATOR + e.getName());
            e.update(MODULE_DIR.PATH);
            broadcastModulePath(e.getModuleDirId(), Arrays.asList(e.getPath().split(MODULE_PATH_SEPARATOR + MODULE_PATH_SEPARATOR)));
        });
    }

    @Override
    public DeleteModuleDirResponse deleteModuleDir(DeleteModuleDirRequest request) throws ScoreDataAccessException {
        ULong moduleDirId = ULong.valueOf(request.getModuleDirId());
        dslContext().select(MODULE.MODULE_ID).from(MODULE)
                .where(MODULE.MODULE_DIR_ID.eq(moduleDirId))
                .fetchStream()
                .forEach(e -> deleteModule(e.get(MODULE.MODULE_ID)));

        dslContext().select(MODULE_DIR.MODULE_DIR_ID).from(MODULE_DIR)
                .where(MODULE_DIR.PARENT_MODULE_DIR_ID.eq(moduleDirId))
                .fetchStream()
                .forEach(e -> deleteModule(e.get(MODULE_DIR.MODULE_DIR_ID)));

        dslContext().delete(MODULE_DIR).where(MODULE_DIR.MODULE_DIR_ID.eq(moduleDirId));

        return new DeleteModuleDirResponse();
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
        ULong inserted = copyModuleDir(requesterUserId, moduleDirRecord, ULong.valueOf(request.getCopyPosDirId()), moduleSetRecord, timestamp);

        if (hasDuplicateName(request.getCopyPosDirId(), moduleDirRecord.getName())) {
            UpdateModuleDirRequest moduleDirRequest = new UpdateModuleDirRequest(requester);
            moduleDirRequest.setModuleDirId(inserted.toBigInteger());
            moduleDirRequest.setName(moduleDirRecord.getName() + " (" + ScoreGuidUtils.randomGuid().substring(0, 4) + ")");
            updateModuleDir(moduleDirRequest);
        }
    }

    private ULong copyModuleDir(ULong requesterId, ModuleDirRecord moduleDirRecord, ULong parentModuleDirId,
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

        copyModuleAndAssign(requesterId, moduleDirRecord.getModuleDirId(), insertedModuleDirId, moduleSetRecord.getModuleSetId(), timestamp);

        getModuleDirByParentModuleDirId(moduleDirRecord.getModuleDirId()).forEach(dir -> {
            copyModuleDir(requesterId, dir, insertedModuleDirId, moduleSetRecord, timestamp);
        });

        return insertedModuleDirId;
    }

    @Override
    public void copyModule(CopyModuleRequest request) throws ScoreDataAccessException {

        ScoreUser requester = request.getRequester();
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        ModuleRecord moduleRecord = dslContext().selectFrom(MODULE)
                .where(MODULE.MODULE_ID.eq(ULong.valueOf(request.getModuleId()))).fetchOne();

        String name = moduleRecord.getName();

        if (hasDuplicateName(request.getCopyPosDirId(), moduleRecord.getName())) {
            name = moduleRecord.getName() + " (" + ScoreGuidUtils.randomGuid().substring(0, 4) + ")";
        }

        ULong insertedModuleId = dslContext().insertInto(MODULE)
                .set(MODULE.MODULE_DIR_ID, ULong.valueOf(request.getCopyPosDirId()))
                .set(MODULE.NAME, name)
                .set(MODULE.MODULE_SET_ID, ULong.valueOf(request.getModuleSetId()))
                .set(MODULE.NAMESPACE_ID, moduleRecord.getNamespaceId())
                .set(MODULE.VERSION_NUM, moduleRecord.getVersionNum())
                .set(MODULE.CREATED_BY, requesterUserId)
                .set(MODULE.OWNER_USER_ID, requesterUserId)
                .set(MODULE.LAST_UPDATED_BY, requesterUserId)
                .set(MODULE.CREATION_TIMESTAMP, timestamp)
                .set(MODULE.LAST_UPDATE_TIMESTAMP, timestamp)
                .returning().fetchOne().getModuleId();
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
        });
    }

    private List<ModuleDirRecord> getModuleDirByParentModuleDirId(ULong parentModuleDirId) {
        return dslContext().selectFrom(MODULE_DIR).where(MODULE_DIR.PARENT_MODULE_DIR_ID.eq(parentModuleDirId)).fetch();
    }

    private List<ModuleRecord> getModuleByModuleDir(ULong moduleDirId) {
        return dslContext().selectFrom(MODULE).where(MODULE.MODULE_DIR_ID.eq(moduleDirId)).fetch();
    }

    private boolean hasDuplicateName(BigInteger parentModuleDirId, String name) {
        if (dslContext().selectFrom(MODULE_DIR)
                .where(and(MODULE_DIR.PARENT_MODULE_DIR_ID.eq(ULong.valueOf(parentModuleDirId)),
                        MODULE_DIR.NAME.eq(name))).fetch().size() > 0)
            return true;
        if (dslContext().selectFrom(MODULE)
                .where(and(MODULE.MODULE_DIR_ID.eq(ULong.valueOf(parentModuleDirId)),
                        MODULE.NAME.eq(name))).fetch().size() > 0) {
            return true;
        }
        return false;
    }
}
