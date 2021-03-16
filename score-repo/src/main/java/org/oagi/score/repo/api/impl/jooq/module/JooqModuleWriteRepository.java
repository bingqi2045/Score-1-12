package org.oagi.score.repo.api.impl.jooq.module;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
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

        if (hasDuplicateName(request.getParentModuleId(), request.getName())) {
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

        ModuleRecord parent = null;
        if (request.getParentModuleId() != null) {
            parent = dslContext().selectFrom(MODULE)
                    .where(MODULE.MODULE_ID.eq(ULong.valueOf(request.getParentModuleId()))).fetchOne();
        }

        String path = parent != null ? parent.getPath() + MODULE_PATH_SEPARATOR + request.getName() : request.getName();

        ModuleRecord moduleRecord = dslContext().insertInto(MODULE)
                .set(MODULE.PARENT_MODULE_ID, parent != null ? parent.getModuleId() : null)
                .set(MODULE.PATH, path)
                .set(MODULE.TYPE, request.getModuleType().name())
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
        module.setParentModuleId(moduleRecord.getParentModuleId().toBigInteger());
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

        if (request.getNamespaceId() != null) {
            moduleRecord.setNamespaceId(ULong.valueOf(request.getNamespaceId()));
        }

        if (StringUtils.hasLength(request.getVersionNum())) {
            moduleRecord.setVersionNum(request.getVersionNum());
        }

        boolean nameChanged = false;
        List<String> tokens = Arrays.asList(moduleRecord.getPath().split(MODULE_PATH_SEPARATOR + MODULE_PATH_SEPARATOR));

        if (StringUtils.hasLength(request.getName()) && !moduleRecord.getName().equals(request.getName())) {
            if (dslContext().selectFrom(MODULE)
                    .where(and(MODULE.PARENT_MODULE_ID.eq(moduleRecord.getParentModuleId()),
                            MODULE.MODULE_ID.notEqual(moduleRecord.getModuleId()),
                            MODULE.NAME.eq(request.getName()))).fetch().size() > 0) {
                throw new IllegalArgumentException("Duplicate module name exist.");
            }
            tokens.set(tokens.size() - 1, request.getName());

            moduleRecord.setName(request.getName());
            moduleRecord.setPath(String.join(MODULE_PATH_SEPARATOR, tokens));
            nameChanged = true;
        }



        if (moduleRecord.changed()) {
            moduleRecord.setLastUpdatedBy(requesterUserId);
            moduleRecord.setLastUpdateTimestamp(timestamp);
            moduleRecord.update();
            if (nameChanged && moduleRecord.getType().equals(ModuleType.DIRECTORY)) {
                broadcastModulePath(moduleRecord.getModuleId(), tokens);
            }
        }

        Module module = new Module();
        module.setModuleId(moduleRecord.getModuleId().toBigInteger());
        module.setParentModuleId(moduleRecord.getParentModuleId().toBigInteger());
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

    private void broadcastModulePath(ULong parentModuleId, List<String> tokens) {
        List<ModuleRecord> moduleRecordList = dslContext().selectFrom(MODULE)
                .where(and(MODULE.PARENT_MODULE_ID.eq(parentModuleId), MODULE.TYPE.eq(ModuleType.DIRECTORY.name())))
                .fetch();

        moduleRecordList.forEach(e -> {
            e.setPath(String.join(MODULE_PATH_SEPARATOR, tokens) + MODULE_PATH_SEPARATOR + e.getName());
            e.update(MODULE.PATH);
            broadcastModulePath(e.getModuleId(), Arrays.asList(e.getPath().split(MODULE_PATH_SEPARATOR + MODULE_PATH_SEPARATOR)));
        });
    }

    @Override
    public void copyModuleDir(CopyModuleDirRequest request) throws ScoreDataAccessException {
        ScoreUser requester = request.getRequester();
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        ModuleSetRecord moduleSetRecord = dslContext().selectFrom(MODULE_SET)
                .where(MODULE_SET.MODULE_SET_ID.eq(ULong.valueOf(request.getModuleSetId()))).fetchOne();

//        if (moduleSetRecord == null) {
//            throw new IllegalArgumentException("Can not found ModuleSet");
//        }
//
//        ModuleDirRecord moduleDirRecord = dslContext().selectFrom(MODULE_DIR)
//                .where(MODULE_DIR.MODULE_DIR_ID.eq(ULong.valueOf(request.getModuleDirId()))).fetchOne();
//
//        if (moduleDirRecord == null) {
//            throw new IllegalArgumentException("Can not found ModuleDir");
//        }
//        ULong inserted = copyModuleDir(requesterUserId, moduleDirRecord, ULong.valueOf(request.getCopyPosDirId()), moduleSetRecord, timestamp);
//
//        if (hasDuplicateName(request.getCopyPosDirId(), moduleDirRecord.getName())) {
//            UpdateModuleDirRequest moduleDirRequest = new UpdateModuleDirRequest(requester);
//            moduleDirRequest.setModuleDirId(inserted.toBigInteger());
//            moduleDirRequest.setName(moduleDirRecord.getName() + " (" + ScoreGuidUtils.randomGuid().substring(0, 4) + ")");
//            updateModuleDir(moduleDirRequest);
//        }
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
                .set(MODULE.PARENT_MODULE_ID, ULong.valueOf(request.getCopyPosDirId()))
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
        getModuleByParent(prevModuleDirId).forEach(m -> {
            ULong insertedModuleId = dslContext().insertInto(MODULE)
                    .set(MODULE.PARENT_MODULE_ID, nextModuleDirId)
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


    private List<ModuleRecord> getModuleByParent(ULong parentModuleId) {
        return dslContext().selectFrom(MODULE).where(MODULE.PARENT_MODULE_ID.eq(parentModuleId)).fetch();
    }

    private boolean hasDuplicateName(BigInteger parentModuleId, String name) {
        if (dslContext().selectFrom(MODULE)
                .where(and(MODULE.PARENT_MODULE_ID.eq(ULong.valueOf(parentModuleId)),
                        MODULE.NAME.eq(name))).fetch().size() > 0) {
            return true;
        }
        return false;
    }
}
