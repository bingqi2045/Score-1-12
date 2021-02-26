package org.oagi.score.gateway.http.api.module_management.controller;

import org.oagi.score.gateway.http.api.module_management.data.ModuleList;
import org.oagi.score.gateway.http.api.module_management.data.SimpleModule;
import org.oagi.score.gateway.http.api.module_management.service.ModuleService;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.repo.api.base.Response;
import org.oagi.score.repo.api.module.model.*;
import org.oagi.score.repo.api.module.model.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/simple_modules", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SimpleModule> getSimpleModules() {
        return moduleService.getSimpleModules();
    }

    @RequestMapping(value = "/module_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModuleList> getModuleList(@AuthenticationPrincipal AuthenticatedPrincipal user) {
        return moduleService.getModuleList(user);
    }

    @RequestMapping(value = "/module/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Module getModule(@AuthenticationPrincipal AuthenticatedPrincipal user,
                            @PathVariable("id") long moduleId) {
        return moduleService.getModule(user, moduleId);
    }

    @RequestMapping(value = "/modules", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ModuleElement getModuleElement(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                          @RequestParam(name = "moduleSetId", required = false) BigInteger moduleSetId,
                                          @RequestParam(name = "parentModuleDirId", required = false) BigInteger parentModuleDirId) {
        GetModuleElementRequest request = new GetModuleElementRequest(sessionService.asScoreUser(user));
        request.setModuleSetId(moduleSetId);
        return moduleService.getModuleElements(request);
    }

    @RequestMapping(value = "/module", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateModuleResponse createModule(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                                 @RequestBody Module module,
                                                 @RequestParam(name = "moduleSetId", required = false) BigInteger moduleSetId) {

        CreateModuleRequest request = new CreateModuleRequest(sessionService.asScoreUser(user));
        request.setModuleDirId(module.getModuleDirId());
        request.setName(module.getName());
        request.setNamespaceId(module.getNamespaceId());
        request.setVersionNum(module.getVersionNum());
        request.setModuleSetId(moduleSetId);
        return moduleService.createModule(request);
    }

    @RequestMapping(value = "/module_dir", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateModuleDirResponse createModuleDir(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                             @RequestBody ModuleDir moduleDir) {

        CreateModuleDirRequest request = new CreateModuleDirRequest(sessionService.asScoreUser(user));
        request.setName(moduleDir.getName());
        request.setParentModuleDirId(moduleDir.getParentModuleDirId());
        return moduleService.createModuleDir(request);
    }


    @RequestMapping(value = "/module_dir/copy", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void copyModuleDir(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                  @RequestBody CopyModuleDirRequest request) {
        request.setRequester(sessionService.asScoreUser(user));
        moduleService.copyModuleDir(request);
    }

    @RequestMapping(value = "/module/copy", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void copyModule(@AuthenticationPrincipal AuthenticatedPrincipal user,
                               @RequestBody CopyModuleRequest request) {
        request.setRequester(sessionService.asScoreUser(user));
        moduleService.copyModule(request);
    }

    @RequestMapping(value = "/module/{id}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UpdateModuleResponse updateModule(@AuthenticationPrincipal AuthenticatedPrincipal user,
                           @PathVariable("id") BigInteger moduleId,
                           @RequestBody Module module) {
        UpdateModuleRequest request = new UpdateModuleRequest(sessionService.asScoreUser(user));
        request.setModuleId(moduleId);
        request.setName(module.getName());
        request.setNamespaceId(module.getNamespaceId());
        request.setVersionNum(module.getVersionNum());
        return moduleService.updateModule(request);
    }

    @RequestMapping(value = "/module_dir/{id}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UpdateModuleDirResponse updateModuleDir(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                             @PathVariable("id") BigInteger moduleDirId,
                                             @RequestBody ModuleDir moduleDir) {
        UpdateModuleDirRequest request = new UpdateModuleDirRequest(sessionService.asScoreUser(user));
        request.setModuleDirId(moduleDirId);
        request.setName(moduleDir.getName());
        return moduleService.updateModuleDir(request);
    }
}
