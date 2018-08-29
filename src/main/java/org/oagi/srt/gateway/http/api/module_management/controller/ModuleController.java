package org.oagi.srt.gateway.http.api.module_management.controller;

import org.oagi.srt.gateway.http.api.module_management.data.ModuleList;
import org.oagi.srt.gateway.http.api.module_management.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @RequestMapping(value = "/module_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<ModuleList> getModuleList(@AuthenticationPrincipal User user) {
        return moduleService.getModuleList(user);
    }
}
