package org.oagi.srt.gateway.http.api.release_management.controller;

import org.oagi.srt.data.ReleaseState;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.release_management.data.*;
import org.oagi.srt.gateway.http.api.release_management.service.ReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ReleaseController {

    @Autowired
    private ReleaseService service;

    @RequestMapping(value = "/simple_releases", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SimpleRelease> getSimpleReleases() {
        return service.getSimpleReleases();
    }

    @RequestMapping(value = "/simple_release/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SimpleRelease getSimpleRelease(@PathVariable("id") long releaseId) {
        return service.getSimpleReleaseByReleaseId(releaseId);
    }

    @RequestMapping(value = "/release_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ReleaseList> getReleaseList(@AuthenticationPrincipal User user) {
        return service.getReleaseList(user);
    }

    @RequestMapping(value = "/releases",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<ReleaseList> getReleases(@AuthenticationPrincipal User user,
                                                 @RequestParam(name = "releaseNum", required = false) String releaseNum,
                                                 @RequestParam(name = "namespace", required = false) String namespace,
                                                 @RequestParam(name = "states", required = false) String states,
                                                 @RequestParam(name = "excludes", required = false) String excludes,
                                                 @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
                                                 @RequestParam(name = "updateStart", required = false) String updateStart,
                                                 @RequestParam(name = "updateEnd", required = false) String updateEnd,
                                                 @RequestParam(name = "sortActive") String sortActive,
                                                 @RequestParam(name = "sortDirection") String sortDirection,
                                                 @RequestParam(name = "pageIndex") int pageIndex,
                                                 @RequestParam(name = "pageSize") int pageSize) {

        ReleaseListRequest request = new ReleaseListRequest();

        request.setReleaseNum(releaseNum);
        request.setNamespace(namespace);
        request.setStates(!StringUtils.isEmpty(states) ?
                Arrays.asList(states.split(",")).stream()
                        .map(e -> ReleaseState.valueOf(e)).collect(Collectors.toList()) : Collections.emptyList());
        request.setExcludes(StringUtils.isEmpty(excludes) ? Collections.emptyList() :
                Arrays.asList(excludes.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).collect(Collectors.toList()));
        request.setUpdaterLoginIds(StringUtils.isEmpty(updaterLoginIds) ? Collections.emptyList() :
                Arrays.asList(updaterLoginIds.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).collect(Collectors.toList()));

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
        return service.getReleases(user, request);
    }

    @RequestMapping(value = "/release/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ReleaseResponse createRelease(@AuthenticationPrincipal User user, @RequestBody ReleaseDetail releaseDetail) {
        return service.createRelease(user, releaseDetail);
    }
}
