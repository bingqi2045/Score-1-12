package org.oagi.score.gateway.http.api.module_management.controller;

import org.oagi.score.gateway.http.api.module_management.data.Module;
import org.oagi.score.gateway.http.api.module_management.data.ModuleList;
import org.oagi.score.gateway.http.api.module_management.data.SimpleModule;
import org.oagi.score.gateway.http.api.module_management.data.module_edit.ModuleElement;
import org.oagi.score.gateway.http.api.module_management.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ModuleSetReleaseController {

    @Autowired
    private ModuleService moduleService;

    @RequestMapping(value = "/module_set_release_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModuleList> getModuleList(@AuthenticationPrincipal AuthenticatedPrincipal user) {
        return moduleService.getModuleList(user);
    }
}
