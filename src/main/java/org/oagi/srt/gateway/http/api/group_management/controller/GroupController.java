package org.oagi.srt.gateway.http.api.group_management.controller;

import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.group_management.data.AppGroup;
import org.oagi.srt.gateway.http.api.group_management.data.GroupListRequest;
import org.oagi.srt.gateway.http.api.group_management.service.GroupService;
import org.oagi.srt.repo.PaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class GroupController {

    @Autowired
    private GroupService service;

    @RequestMapping(value = "/group/list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PaginationResponse<AppGroup> getGroups(@AuthenticationPrincipal User user,
                                                  @RequestParam(name = "name", required = false) String name,
                                                  @RequestParam(name = "sortActive") String sortActive,
                                                  @RequestParam(name = "sortDirection") String sortDirection,
                                                  @RequestParam(name = "pageIndex") int pageIndex,
                                                  @RequestParam(name = "pageSize") int pageSize) {

        GroupListRequest request = new GroupListRequest();
        request.setName(name);
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSortActive(sortActive);
        pageRequest.setSortDirection(sortDirection);
        pageRequest.setPageIndex(pageIndex);
        pageRequest.setPageSize(pageSize);
        request.setPageRequest(pageRequest);
        return service.getGroupList(request);
    }

    @RequestMapping(value = "/group/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AppGroup getGroups(@AuthenticationPrincipal User user,
                              @PathVariable(name = "id", required = true) int appGroupId) {

        return service.getGroup(appGroupId);
    }

    @RequestMapping(value = "/group/{id}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AppGroup updateGroups(@AuthenticationPrincipal User user,
                                 @PathVariable(name = "id", required = true) int appGroupId,
                                 @RequestBody Map<String, String> body) {

        String name = body.getOrDefault("name", "");
        String groupUsers = body.getOrDefault("groupUsers", "");
        List<String> groupUserList = Arrays.asList(groupUsers.split(","));
        String groupPermissions = body.getOrDefault("groupPermissions", "");
        List<String> groupPermissionList = Arrays.asList(groupPermissions.split(","));

        return service.updateGroup(appGroupId, name, groupUserList, groupPermissionList);
    }
}
