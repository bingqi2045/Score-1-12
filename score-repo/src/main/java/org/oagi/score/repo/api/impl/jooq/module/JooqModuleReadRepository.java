package org.oagi.score.repo.api.impl.jooq.module;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.module.model.*;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.repo.api.module.ModuleReadRepository;
import org.oagi.score.repo.api.module.model.Module;
import org.oagi.score.repo.api.security.AccessControl;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.time.ZoneId;
import java.util.*;

import static org.oagi.score.repo.api.base.SortDirection.ASC;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.impl.jooq.utils.DSLUtils.contains;
import static org.oagi.score.repo.api.impl.jooq.utils.DSLUtils.isNull;
import static org.oagi.score.repo.api.impl.utils.StringUtils.trim;
import static org.oagi.score.repo.api.user.model.ScoreRole.DEVELOPER;
import static org.oagi.score.repo.api.user.model.ScoreRole.END_USER;

public class JooqModuleReadRepository
        extends JooqScoreRepository
        implements ModuleReadRepository {

    public JooqModuleReadRepository(DSLContext dslContext) {
        super(dslContext);
    }

    private SelectOnConditionStep select() {
        return dslContext().select(
                MODULE.MODULE_ID,
                MODULE.MODULE_DIR_ID,
                MODULE_DIR.PATH,
                MODULE_DIR.NAME.as("dir_name"),
                MODULE.NAME,
                MODULE.VERSION_NUM,
                MODULE.NAMESPACE_ID,
                NAMESPACE.URI,
                APP_USER.as("creator").APP_USER_ID.as("creator_user_id"),
                APP_USER.as("creator").LOGIN_ID.as("creator_login_id"),
                APP_USER.as("creator").IS_DEVELOPER.as("creator_is_developer"),
                APP_USER.as("updater").APP_USER_ID.as("updater_user_id"),
                APP_USER.as("updater").LOGIN_ID.as("updater_login_id"),
                APP_USER.as("updater").IS_DEVELOPER.as("updater_is_developer"),
                MODULE.CREATION_TIMESTAMP,
                MODULE.LAST_UPDATE_TIMESTAMP,
                MODULE_SET_ASSIGNMENT.MODULE_SET_ASSIGNMENT_ID)
                .from(MODULE)
                .join(MODULE_DIR).on(MODULE.MODULE_DIR_ID.eq(MODULE_DIR.MODULE_DIR_ID))
                .join(NAMESPACE).on(NAMESPACE.NAMESPACE_ID.eq(MODULE.NAMESPACE_ID))
                .join(APP_USER.as("creator")).on(MODULE.CREATED_BY.eq(APP_USER.as("creator").APP_USER_ID))
                .join(APP_USER.as("updater")).on(MODULE.LAST_UPDATED_BY.eq(APP_USER.as("updater").APP_USER_ID))
                .leftJoin(MODULE_SET_ASSIGNMENT).on(MODULE.MODULE_ID.eq(MODULE_SET_ASSIGNMENT.MODULE_ID));
    }

    private RecordMapper<Record, Module> mapper() {
        return record -> {
            Module module = new Module();
            module.setModuleId(record.get(MODULE.MODULE_ID).toBigInteger());
            module.setModuleDirId(record.get(MODULE.MODULE_DIR_ID).toBigInteger());
            module.setPath(record.get(MODULE_DIR.PATH).concat("\\").concat(record.get(MODULE.NAME)));
            module.setNamespaceUri(record.get(NAMESPACE.URI));
            module.setNamespaceId(record.get(MODULE.NAMESPACE_ID).toBigInteger());
            module.setName(record.get(MODULE.NAME));
            module.setVersionNum(record.get(MODULE.VERSION_NUM));
//
//            module.setCreatedBy(new ScoreUser(
//                    record.get(APP_USER.as("creator").APP_USER_ID.as("creator_user_id")).toBigInteger(),
//                    record.get(APP_USER.as("creator").LOGIN_ID.as("creator_login_id")),
//                    (byte) 1 == record.get(APP_USER.as("creator").IS_DEVELOPER.as("creator_is_developer")) ? DEVELOPER : END_USER
//            ));
//            module.setLastUpdatedBy(new ScoreUser(
//                    record.get(APP_USER.as("updater").APP_USER_ID.as("updater_user_id")).toBigInteger(),
//                    record.get(APP_USER.as("updater").LOGIN_ID.as("updater_login_id")),
//                    (byte) 1 == record.get(APP_USER.as("updater").IS_DEVELOPER.as("updater_is_developer")) ? DEVELOPER : END_USER
//            ));
            module.setCreationTimestamp(
                    Date.from(record.get(MODULE.CREATION_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
            module.setLastUpdateTimestamp(
                    Date.from(record.get(MODULE.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
            return module;
        };
    }

    private RecordMapper<Record, ModuleDir> moduleDirMapper() {
        return record -> {
            ModuleDir moduleDir = new ModuleDir();
            moduleDir.setModuleDirId(record.get(MODULE_DIR.MODULE_DIR_ID).toBigInteger());
            moduleDir.setName(record.get(MODULE_DIR.NAME));
            if (record.get(MODULE_DIR.PARENT_MODULE_DIR_ID) != null) {
                moduleDir.setParentModuleDirId(record.get(MODULE_DIR.PARENT_MODULE_DIR_ID).toBigInteger());
            }
            moduleDir.setPath(record.get(MODULE_DIR.PATH));

            moduleDir.setCreationTimestamp(
                    Date.from(record.get(MODULE_DIR.CREATION_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
            moduleDir.setLastUpdateTimestamp(
                    Date.from(record.get(MODULE_DIR.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
            return moduleDir;
        };
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public GetModuleResponse getModule(GetModuleRequest request) throws ScoreDataAccessException {
        Module module = null;

        BigInteger moduleId = request.getModuleId();
        if (!isNull(moduleId)) {
            module = (Module) select()
                    .where(MODULE.MODULE_ID.eq(ULong.valueOf(moduleId)))
                    .fetchOne(mapper());
        }

        return new GetModuleResponse(module);
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public GetModuleListResponse getModuleList(GetModuleListRequest request) throws ScoreDataAccessException {
        Collection<Condition> conditions = getConditions(request);

        SelectConditionStep conditionStep;

        conditionStep = select().where(conditions);

        SortField sortField = getSortField(request);
        int length = dslContext().fetchCount(conditionStep);
        SelectFinalStep finalStep;
        if (sortField == null) {
            if (request.isPagination()) {
                finalStep = conditionStep.limit(request.getPageOffset(), request.getPageSize());
            } else {
                finalStep = conditionStep;
            }
        } else {
            if (request.isPagination()) {
                finalStep = conditionStep.orderBy(sortField)
                        .limit(request.getPageOffset(), request.getPageSize());
            } else {
                finalStep = conditionStep.orderBy(sortField);
            }
        }

        return new GetModuleListResponse(
                finalStep.fetch(mapper()),
                request.getPageIndex(),
                request.getPageSize(),
                length
        );
    }

    private Collection<Condition> getConditions(GetModuleListRequest request) {
        List<Condition> conditions = new ArrayList();

        if (request.getModuleSetId() != null) {
            conditions.add(MODULE_SET_ASSIGNMENT.MODULE_SET_ID.eq(
                    ULong.valueOf(request.getModuleSetId())
            ));
        }

        if (StringUtils.hasLength(request.getName())) {
            conditions.addAll(contains(request.getName(), BIZ_CTX.NAME));
        }

        return conditions;
    }

    private SortField getSortField(GetModuleListRequest request) {
        if (!StringUtils.hasLength(request.getSortActive())) {
            return null;
        }

        Field field;
        switch (trim(request.getSortActive()).toLowerCase()) {
            case "name":
                field = MODULE.NAME;
                break;

            case "lastupdatetimestamp":
                field = MODULE.LAST_UPDATE_TIMESTAMP;
                break;

            default:
                return null;
        }

        return (request.getSortDirection() == ASC) ? field.asc() : field.desc();
    }

    private SelectOnConditionStep selectModuleElement() {
        return dslContext().select(
                MODULE.MODULE_ID,
                MODULE.NAME,
                MODULE.NAMESPACE_ID,
                NAMESPACE.URI,
                MODULE.VERSION_NUM,
                APP_USER.as("creator").APP_USER_ID.as("creator_user_id"),
                APP_USER.as("creator").LOGIN_ID.as("creator_login_id"),
                APP_USER.as("creator").IS_DEVELOPER.as("creator_is_developer"),
                APP_USER.as("updater").APP_USER_ID.as("updater_user_id"),
                APP_USER.as("updater").LOGIN_ID.as("updater_login_id"),
                APP_USER.as("updater").IS_DEVELOPER.as("updater_is_developer"),
                MODULE.CREATION_TIMESTAMP,
                MODULE.LAST_UPDATE_TIMESTAMP)
                .from(MODULE)
                .join(MODULE_DIR).on(MODULE.MODULE_DIR_ID.eq(MODULE_DIR.MODULE_DIR_ID))
                .join(NAMESPACE).on(NAMESPACE.NAMESPACE_ID.eq(MODULE.NAMESPACE_ID))
                .join(APP_USER.as("creator")).on(MODULE.CREATED_BY.eq(APP_USER.as("creator").APP_USER_ID))
                .join(APP_USER.as("updater")).on(MODULE.LAST_UPDATED_BY.eq(APP_USER.as("updater").APP_USER_ID))
                .leftJoin(MODULE_SET_ASSIGNMENT).on(MODULE.MODULE_ID.eq(MODULE_SET_ASSIGNMENT.MODULE_ID));
    }

    private SelectOnConditionStep selectModuleDirElement() {
        return dslContext().select(
                MODULE_DIR.MODULE_DIR_ID,
                MODULE_DIR.NAME,
                MODULE_DIR.PATH,
                APP_USER.as("creator").APP_USER_ID.as("creator_user_id"),
                APP_USER.as("creator").LOGIN_ID.as("creator_login_id"),
                APP_USER.as("creator").IS_DEVELOPER.as("creator_is_developer"),
                APP_USER.as("updater").APP_USER_ID.as("updater_user_id"),
                APP_USER.as("updater").LOGIN_ID.as("updater_login_id"),
                APP_USER.as("updater").IS_DEVELOPER.as("updater_is_developer"),
                MODULE_DIR.CREATION_TIMESTAMP,
                MODULE_DIR.LAST_UPDATE_TIMESTAMP)
                .from(MODULE_DIR)
                .join(MODULE_DIR.as("parent")).on(MODULE_DIR.PARENT_MODULE_DIR_ID.eq(MODULE_DIR.as("parent").MODULE_DIR_ID))
                .join(APP_USER.as("creator")).on(MODULE_DIR.CREATED_BY.eq(APP_USER.as("creator").APP_USER_ID))
                .join(APP_USER.as("updater")).on(MODULE_DIR.LAST_UPDATED_BY.eq(APP_USER.as("updater").APP_USER_ID));
    }

    @Override
    public GetModuleElementResponse getModuleElements(GetModuleElementRequest request) throws ScoreDataAccessException {

        SelectOnConditionStep stepModuleDir = selectModuleDirElement();

        List<Condition> moduleDirConditions = new ArrayList();

        SelectOnConditionStep stepModule = selectModuleElement();

        List<Condition> moduleConditions = new ArrayList();

        if (request.getModuleSetId() != null) {
            moduleConditions.add(MODULE_SET_ASSIGNMENT.MODULE_SET_ID.eq(ULong.valueOf(request.getModuleSetId())));
            stepModuleDir = stepModuleDir.leftJoin(MODULE).on(MODULE_DIR.MODULE_DIR_ID.eq(MODULE_DIR.MODULE_DIR_ID))
                    .leftJoin(MODULE_SET_ASSIGNMENT).on(MODULE.MODULE_ID.eq(MODULE_SET_ASSIGNMENT.MODULE_ID));
            moduleDirConditions.add(MODULE_SET_ASSIGNMENT.MODULE_SET_ID.eq(ULong.valueOf(request.getModuleSetId())));
        }

        if (request.getModuleDirId() != null) {
            moduleConditions.add(MODULE_DIR.MODULE_DIR_ID.eq(ULong.valueOf(request.getModuleDirId())));
            moduleDirConditions.add(MODULE_DIR.PARENT_MODULE_DIR_ID.eq(ULong.valueOf(request.getModuleDirId())));
        } else {
            moduleConditions.add(MODULE_DIR.PARENT_MODULE_DIR_ID.isNull());
            moduleDirConditions.add(MODULE_DIR.as("parent").PARENT_MODULE_DIR_ID.isNull());
        }

        List<ModuleElement> elements = stepModuleDir.where(moduleDirConditions).fetch(record -> {
            ModuleElement moduleElement = new ModuleElement();
            moduleElement.setDirectory(true);
            moduleElement.setId(record.get(MODULE_DIR.MODULE_DIR_ID).toBigInteger());
            moduleElement.setName(record.get(MODULE_DIR.NAME));
            moduleElement.setPath(record.get(MODULE_DIR.PATH));
            return moduleElement;
        });

        elements.addAll(stepModule.where(moduleConditions).fetch(record -> {
            ModuleElement moduleElement = new ModuleElement();
            moduleElement.setDirectory(false);
            moduleElement.setId(record.get(MODULE.MODULE_ID).toBigInteger());
            moduleElement.setName(record.get(MODULE.NAME));
            moduleElement.setPath(record.get(MODULE_DIR.PATH));
            moduleElement.setNamespaceId(record.get(NAMESPACE.NAMESPACE_ID).toBigInteger());
            moduleElement.setNamespaceUri(record.get(NAMESPACE.URI));
            return moduleElement;
        }));

        GetModuleElementResponse response = new GetModuleElementResponse();
        response.setElements(elements);
        return response;
    }

    @Override
    public List<Module> getAllModules(GetModuleListRequest request) throws ScoreDataAccessException {
        if (request.getModuleSetId() == null) {
            return dslContext().select(
                    MODULE.MODULE_ID,
                    MODULE.MODULE_DIR_ID,
                    MODULE_DIR.PATH,
                    MODULE.NAME,
                    MODULE.VERSION_NUM,
                    MODULE.NAMESPACE_ID,
                    NAMESPACE.URI,
                    MODULE.CREATION_TIMESTAMP,
                    MODULE.LAST_UPDATE_TIMESTAMP)
                    .from(MODULE)
                    .join(MODULE_DIR).on(MODULE.MODULE_DIR_ID.eq(MODULE_DIR.MODULE_DIR_ID))
                    .join(NAMESPACE).on(NAMESPACE.NAMESPACE_ID.eq(MODULE.NAMESPACE_ID))
                    .fetch(mapper());
        } else {
            return dslContext().select(
                    MODULE.MODULE_ID,
                    MODULE.MODULE_DIR_ID,
                    MODULE_DIR.PATH,
                    MODULE.NAME,
                    MODULE.VERSION_NUM,
                    MODULE.NAMESPACE_ID,
                    NAMESPACE.URI,
                    MODULE.CREATION_TIMESTAMP,
                    MODULE.LAST_UPDATE_TIMESTAMP)
                    .from(MODULE)
                    .join(MODULE_DIR).on(MODULE.MODULE_DIR_ID.eq(MODULE_DIR.MODULE_DIR_ID))
                    .join(NAMESPACE).on(NAMESPACE.NAMESPACE_ID.eq(MODULE.NAMESPACE_ID))
                    .join(MODULE_SET_ASSIGNMENT).on(MODULE_SET_ASSIGNMENT.MODULE_ID.eq(MODULE.MODULE_ID))
                    .where(MODULE_SET_ASSIGNMENT.MODULE_SET_ID.eq(ULong.valueOf(request.getModuleSetId())))
                    .fetch(mapper());
        }
    }

    @Override
    public List<ModuleDir> getAllModuleDirs(GetModuleListRequest request) throws ScoreDataAccessException {
        return dslContext().selectFrom(MODULE_DIR)
                .fetch(moduleDirMapper());
    }
}
