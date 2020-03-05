package org.oagi.srt.gateway.http.api.permission_management.controller;

import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.permission_management.data.AppPermission;
import org.oagi.srt.gateway.http.api.permission_management.data.PermissionListRequest;
import org.oagi.srt.gateway.http.api.permission_management.service.PermissionService;
import org.oagi.srt.repo.PaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class PermissionController {

    @Autowired
    private PermissionService service;

    @RequestMapping(value = "/permission/list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PaginationResponse<AppPermission> getPermissions(@AuthenticationPrincipal User user,
                                                            @RequestParam(name = "segment", required = false) String segment,
                                                            @RequestParam(name = "object", required = false) String object,
                                                            @RequestParam(name = "sortActive") String sortActive,
                                                            @RequestParam(name = "sortDirection") String sortDirection,
                                                            @RequestParam(name = "pageIndex") int pageIndex,
                                                            @RequestParam(name = "pageSize") int pageSize) {

        PermissionListRequest request = new PermissionListRequest();
        request.setObject(object);
        request.setSegment(segment);
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSortActive(sortActive);
        pageRequest.setSortDirection(sortDirection);
        pageRequest.setPageIndex(pageIndex);
        pageRequest.setPageSize(pageSize);
        request.setPageRequest(pageRequest);
        return service.getPermissionList(request);
    }

    @RequestMapping(value = "/permission/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AppPermission getPermissions(@AuthenticationPrincipal User user,
                                        @PathVariable(name = "id", required = true) int appPermissionId) {

        return service.getPermission(appPermissionId);
    }

    @RequestMapping(value = "/permission/{id}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AppPermission updatePermissions(@AuthenticationPrincipal User user,
                                           @PathVariable(name = "id", required = true) int appPermissionId,
                                           @RequestBody Map<String, String> body) {

        String segment = body.getOrDefault("segment", "");
        String object = body.getOrDefault("object", "");
        String operation = body.getOrDefault("operation", "");
        String description = body.getOrDefault("description", "");

        return service.updatePermission(appPermissionId, segment, object, operation, description);
    }
}
