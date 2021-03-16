package org.oagi.score.gateway.http.api.module_management.service;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.oagi.score.gateway.http.api.module_management.data.*;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AppUser;
import org.oagi.score.repo.api.module.ModuleReadRepository;
import org.oagi.score.repo.api.module.model.*;
import org.oagi.score.repo.api.module.model.Module;
import org.oagi.score.service.module.ModuleElementContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.inline;

@Service
@Transactional(readOnly = true)
public class ModuleService {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    public List<SimpleModule> getSimpleModules() {
        return dslContext.select(Tables.MODULE.MODULE_ID, Tables.MODULE.NAME)
                .from(Tables.MODULE)
                .fetchInto(SimpleModule.class);
    }

    public List<ModuleList> getModuleList(AuthenticatedPrincipal user) {
        AppUser U1 = Tables.APP_USER;
        AppUser U2 = Tables.APP_USER;
        List<ModuleList> moduleLists = dslContext.select(
                Tables.MODULE.MODULE_ID,
                Tables.MODULE.NAME,
                Tables.MODULE.OWNER_USER_ID,
                Tables.MODULE.LAST_UPDATE_TIMESTAMP,
                Tables.NAMESPACE.URI.as("namespace"),
                U1.LOGIN_ID.as("owner"),
                U2.LOGIN_ID.as("last_updated_by"))
                .from(Tables.MODULE)
                .join(Tables.NAMESPACE)
                .on(Tables.MODULE.NAMESPACE_ID.eq(Tables.NAMESPACE.NAMESPACE_ID))
                .join(U1)
                .on(Tables.MODULE.OWNER_USER_ID.eq(U1.APP_USER_ID))
                .join(U2)
                .on(Tables.MODULE.LAST_UPDATED_BY.eq(U2.APP_USER_ID))
                .fetchInto(ModuleList.class);

        BigInteger userId = sessionService.userId(user);

        moduleLists.stream().forEach(moduleList -> {
            moduleList.setCanEdit(moduleList.getOwnerUserId().equals(userId));
        });

        return moduleLists;
    }

    public Module getModule(AuthenticatedPrincipal user, long moduleId) {
        Module module = dslContext.select(
                Tables.MODULE.MODULE_ID,
                Tables.MODULE.NAME,
                Tables.MODULE.NAMESPACE_ID,
                Tables.MODULE.LAST_UPDATE_TIMESTAMP)
                .from(Tables.MODULE)
                .where(Tables.MODULE.MODULE_ID.eq(ULong.valueOf(moduleId)))
                .fetchOneInto(Module.class);
        return module;
    }
    
    public ModuleElement getModuleElements(GetModuleElementRequest request) {
        ModuleReadRepository repository = scoreRepositoryFactory.createModuleReadRepository();
        ModuleElementContext context = new ModuleElementContext(request.getRequester(), repository, request.getModuleSetId(), null);
        return context.getRootElement();
    }

    @Transactional
    public CreateModuleResponse createModule(CreateModuleRequest request) {
        return scoreRepositoryFactory.createModuleWriteRepository().createModule(request);
    }

    @Transactional
    public void copyModuleDir(CopyModuleDirRequest request) {
        scoreRepositoryFactory.createModuleWriteRepository().copyModuleDir(request);
    }

    @Transactional
    public void copyModule(CopyModuleRequest request) {
        scoreRepositoryFactory.createModuleWriteRepository().copyModule(request);
    }

    @Transactional
    public UpdateModuleResponse updateModule(UpdateModuleRequest request) {
        return scoreRepositoryFactory.createModuleWriteRepository().updateModule(request);
    }

    @Transactional
    public DeleteModuleResponse deleteModule(DeleteModuleRequest request) {
        if(request.getModuleSetId() != null) {
            DeleteModuleSetAssignmentRequest requestUnassign = new DeleteModuleSetAssignmentRequest(request.getRequester());
            requestUnassign.setModuleId(request.getModuleId());
            requestUnassign.setModuleSetId(request.getModuleSetId());
            scoreRepositoryFactory.createModuleSetWriteRepository().unassignModule(requestUnassign);
        }
        return scoreRepositoryFactory.createModuleWriteRepository().deleteModule(request);
    }
}
