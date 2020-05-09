package org.oagi.srt.gateway.http.api.cc_management.controller;

import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListTypes;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.service.CcListService;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.oagi.srt.gateway.http.api.cc_management.data.CcState.Deleted;

@RestController
public class CcListController {

    @Autowired
    private CcListService service;

    @RequestMapping(value = "/core_component", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<CcList> getCcList(
            @RequestParam(name = "releaseId") long releaseId,
            @RequestParam(name = "den", required = false) String den,
            @RequestParam(name = "definition", required = false) String definition,
            @RequestParam(name = "module", required = false) String module,
            @RequestParam(name = "types", required = false) String types,
            @RequestParam(name = "states", required = false) String states,
            @RequestParam(name = "deprecated", required = false) String deprecated,
            @RequestParam(name = "ownerLoginIds", required = false) String ownerLoginIds,
            @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
            @RequestParam(name = "updateStart", required = false) String updateStart,
            @RequestParam(name = "updateEnd", required = false) String updateEnd,
            @RequestParam(name = "componentTypes", required = false) String componentTypes,
            @RequestParam(name = "excludes", required = false) String excludes,
            @RequestParam(name = "sortActive") String sortActive,
            @RequestParam(name = "sortDirection") String sortDirection,
            @RequestParam(name = "pageIndex") int pageIndex,
            @RequestParam(name = "pageSize") int pageSize) {

        CcListRequest request = new CcListRequest();

        request.setReleaseId(releaseId);
        request.setTypes(CcListTypes.fromString(types));
        if (!StringUtils.isEmpty(states)) {
            List<String> stateStrings = Arrays.asList(states.split(",")).stream().collect(Collectors.toList());
            request.setStates(stateStrings.stream()
                    .map(e -> CcState.valueOf(e.trim())).collect(Collectors.toList()));
        } else {
            request.setStates(Stream.of(CcState.values()).filter(e -> e != Deleted).collect(Collectors.toList()));
        }
        if (!StringUtils.isEmpty(deprecated)) {
            if ("true".equalsIgnoreCase(deprecated.toLowerCase())) {
                request.setDeprecated(true);
            } else if ("false".equalsIgnoreCase(deprecated.toLowerCase())) {
                request.setDeprecated(false);
            }
        }
        request.setOwnerLoginIds(StringUtils.isEmpty(ownerLoginIds) ? Collections.emptyList() :
                Arrays.asList(ownerLoginIds.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).collect(Collectors.toList()));
        request.setUpdaterLoginIds(StringUtils.isEmpty(updaterLoginIds) ? Collections.emptyList() :
                Arrays.asList(updaterLoginIds.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).collect(Collectors.toList()));
        request.setDen(den);
        request.setDefinition(definition);
        request.setModule(module);
        request.setComponentTypes(componentTypes);
        request.setExcludes(StringUtils.isEmpty(excludes) ? Collections.emptyList() :
                Arrays.asList(excludes.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).collect(Collectors.toList()));

        if (!StringUtils.isEmpty(updateStart)) {
            request.setUpdateStartDate(new Date(Long.valueOf(updateStart)));
        }
        if (!StringUtils.isEmpty(updateEnd)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(updateEnd));
            calendar.add(Calendar.DATE, 1);
            request.setUpdateEndDate(calendar.getTime());
        }

        PageRequest pageRequest = new PageRequest();
        pageRequest.setSortActive(sortActive);
        pageRequest.setSortDirection(sortDirection);
        pageRequest.setPageIndex(pageIndex);
        pageRequest.setPageSize(pageSize);
        request.setPageRequest(pageRequest);

        return service.getCcList(request);
    }

    @RequestMapping(value = "/core_component/{type}/{manifestId:[\\d]+}/transfer_ownership",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity transferOwnership(@AuthenticationPrincipal User user,
                                            @PathVariable("type") String type,
                                            @PathVariable("manifestId") long manifestId,
                                            @RequestBody Map<String, String> request) {
        String targetLoginId = request.get("targetLoginId");
        service.transferOwnership(user, type, manifestId, targetLoginId);
        return ResponseEntity.noContent().build();
    }
}
