package org.oagi.srt.gateway.http.api.bie_management.controller;

import org.oagi.srt.gateway.http.api.bie_management.data.AsccpForBie;
import org.oagi.srt.gateway.http.api.bie_management.data.BieCreateRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.BieCreateResponse;
import org.oagi.srt.gateway.http.api.bie_management.service.BieService;
import org.oagi.srt.gateway.http.api.module_management.data.SimpleModule;
import org.oagi.srt.gateway.http.api.module_management.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class BieCreateController {

    @Autowired
    private BieService bieService;

    @Autowired
    private ModuleService moduleService;

    @RequestMapping(value = "/profile_bie/asccp/release/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<AsccpForBie> getAsccpListForBie(@PathVariable("id") long releaseId) {

        Map<Long, String> moduleMap = moduleService.getSimpleModules().stream()
                .collect(Collectors.toMap(SimpleModule::getModuleId, SimpleModule::getModule));

        return bieService.getAsccpListForBie(releaseId).stream()
                .map(e -> {
                    AsccpForBie copy = new AsccpForBie();

                    copy.setAsccpId(e.getAsccpId());
                    copy.setPropertyTerm(e.getPropertyTerm());
                    copy.setGuid(e.getGuid());

                    Long moduleId = e.getModuleId();
                    if (moduleId != null) {
                        copy.setModule(moduleMap.get(moduleId));
                    }

                    copy.setLastUpdateTimestamp(e.getLastUpdateTimestamp());

                    return copy;
                }).collect(Collectors.toList());
    }

    @RequestMapping(value = "/profile_bie/create", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieCreateResponse create(
            @AuthenticationPrincipal User user,
            @RequestBody BieCreateRequest bieCreateRequest) {

        BieCreateResponse response = bieService.createBie(user, bieCreateRequest);
        return response;
    }

}
