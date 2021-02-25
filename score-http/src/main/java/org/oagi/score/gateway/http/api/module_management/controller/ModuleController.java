package org.oagi.score.gateway.http.api.module_management.controller;

import org.oagi.score.gateway.http.api.module_management.data.ModuleList;
import org.oagi.score.gateway.http.api.module_management.data.SimpleModule;
import org.oagi.score.gateway.http.api.module_management.service.ModuleService;
import org.oagi.score.gateway.http.configuration.security.SessionService;
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

    @RequestMapping(value = "/module/create", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateModuleResponse getModuleElement(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                                 @RequestBody Module module,
                                                 @RequestParam(name = "moduleSetId", required = false) BigInteger moduleSetId) {

        CreateModuleRequest request = new CreateModuleRequest(sessionService.asScoreUser(user));
        request.setModuleDirId(module.getModuleId());
        request.setName(module.getName());
        request.setNamespaceId(module.getNamespaceId());
        request.setVersionNum(module.getVersionNum());
        request.setModuleSetId(moduleSetId);
        return moduleService.createModule(request);
    }
}
