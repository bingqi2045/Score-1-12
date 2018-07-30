package org.oagi.srt.gateway.http.bie_management;

import org.oagi.srt.gateway.http.module_management.ModuleService;
import org.oagi.srt.gateway.http.module_management.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity create(
            @AuthenticationPrincipal User user,
            @RequestBody BieCreateRequest bieCreateRequest) {

        bieService.createBie(user, bieCreateRequest);
        return ResponseEntity.noContent().build();
    }

}
