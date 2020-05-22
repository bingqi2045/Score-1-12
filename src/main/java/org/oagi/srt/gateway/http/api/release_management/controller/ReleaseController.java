package org.oagi.srt.gateway.http.api.release_management.controller;

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

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ReleaseController {

    @Autowired
    private ReleaseService service;

    @RequestMapping(value = "/simple_releases", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SimpleRelease> getSimpleReleases(@RequestParam(name = "states", required = false) String states) {
        SimpleReleasesRequest request = new SimpleReleasesRequest();

        request.setStates(!StringUtils.isEmpty(states) ?
                Arrays.asList(states.split(",")).stream()
                        .map(e -> ReleaseState.valueOf(e)).collect(Collectors.toList()) : Collections.emptyList());

        return service.getSimpleReleases(request);
    }

    @RequestMapping(value = "/simple_release/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SimpleRelease getSimpleRelease(@PathVariable("id") BigInteger releaseId) {
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
                                                 @RequestParam(name = "states", required = false) String states,
                                                 @RequestParam(name = "excludes", required = false) String excludes,
                                                 @RequestParam(name = "creatorLoginIds", required = false) String creatorLoginIds,
                                                 @RequestParam(name = "createStart", required = false) String createStart,
                                                 @RequestParam(name = "createEnd", required = false) String createEnd,
                                                 @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
                                                 @RequestParam(name = "updateStart", required = false) String updateStart,
                                                 @RequestParam(name = "updateEnd", required = false) String updateEnd,
                                                 @RequestParam(name = "sortActive") String sortActive,
                                                 @RequestParam(name = "sortDirection") String sortDirection,
                                                 @RequestParam(name = "pageIndex") int pageIndex,
                                                 @RequestParam(name = "pageSize") int pageSize) {

        ReleaseListRequest request = new ReleaseListRequest();

        request.setReleaseNum(releaseNum);
        request.setStates(!StringUtils.isEmpty(states) ?
                Arrays.asList(states.split(",")).stream()
                        .map(e -> ReleaseState.valueOf(e)).collect(Collectors.toList()) : Collections.emptyList());
        request.setExcludes(StringUtils.isEmpty(excludes) ? Collections.emptyList() :
                Arrays.asList(excludes.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).collect(Collectors.toList()));
        request.setCreatorLoginIds(StringUtils.isEmpty(creatorLoginIds) ? Collections.emptyList() :
                Arrays.asList(creatorLoginIds.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).collect(Collectors.toList()));

        if (!StringUtils.isEmpty(createStart)) {
            request.setCreateStartDate(new Date(Long.valueOf(createStart)));
        }
        if (!StringUtils.isEmpty(createEnd)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(createEnd));
            calendar.add(Calendar.DATE, 1);
            request.setCreateEndDate(calendar.getTime());
        }
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
    public ReleaseResponse createRelease(@AuthenticationPrincipal User user,
                                         @RequestBody ReleaseDetail releaseDetail) {
        return service.createRelease(user, releaseDetail);
    }

    @RequestMapping(value = "/release/{id}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateRelease(@AuthenticationPrincipal User user,
                              @PathVariable("id") BigInteger releaseId,
                              @RequestBody ReleaseDetail releaseDetail) {
        releaseDetail.setReleaseId(releaseId);
        service.updateRelease(user, releaseDetail);
    }

    @RequestMapping(value = "/release/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ReleaseDetail getReleaseDetail(@AuthenticationPrincipal User user,
                                          @PathVariable("id") BigInteger releaseId) {
        return service.getReleaseDetail(user, releaseId);
    }

    @RequestMapping(value = "/release/{id}/assignable", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AssignComponents assignComponents(@AuthenticationPrincipal User user,
                                             @PathVariable("id") BigInteger releaseId) {
        return service.getAssignComponents(releaseId);
    }

    @RequestMapping(value = "/release/{id}/" +
            "", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void assignComponents(@AuthenticationPrincipal User user,
                                 @PathVariable("id") BigInteger releaseId,
                                 @RequestBody AssignComponentsRequest request) {
        request.setReleaseId(releaseId);
        service.assignComponents(user, request);
    }

    @RequestMapping(value = "/release/{id}/unassign", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void unassignComponents(@AuthenticationPrincipal User user,
                                   @PathVariable("id") BigInteger releaseId,
                                   @RequestBody UnassignComponentsRequest request) {
        request.setReleaseId(releaseId);
        service.unassignComponents(user, request);
    }

    @RequestMapping(value = "/release/{id}/state", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void transitState(@AuthenticationPrincipal User user,
                             @PathVariable("id") BigInteger releaseId,
                             @RequestBody TransitStateRequest request) {
        request.setReleaseId(releaseId);
        service.transitState(user, request);
    }

    @RequestMapping(value = "/release/validate", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ReleaseValidationResponse validate(@AuthenticationPrincipal User user,
                                              @RequestBody ReleaseValidationRequest request) {
        return service.validate(user, request);
    }
}
