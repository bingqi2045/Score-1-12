package org.oagi.srt.gateway.http.api.bie_management.controller;

import org.oagi.srt.data.BieState;
import org.oagi.srt.data.BizCtx;
import org.oagi.srt.gateway.http.api.bie_management.data.BieList;
import org.oagi.srt.gateway.http.api.bie_management.data.BieListRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.DeleteBieListRequest;
import org.oagi.srt.gateway.http.api.bie_management.service.BieService;
import org.oagi.srt.gateway.http.api.common.data.AccessPrivilege;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.context_management.data.BizCtxAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class BieListController {

    @Autowired
    private BieService service;

    @RequestMapping(value = "/bie_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<BieList> getBieList(@AuthenticationPrincipal User user,
                                            @RequestParam(name = "propertyTerm", required = false) String propertyTerm,
                                            @RequestParam(name = "businessContext", required = false) String businessContext,
                                            @RequestParam(name = "accManifestId", required = false) BigInteger accManifestId,
                                            @RequestParam(name = "access", required = false) String access,
                                            @RequestParam(name = "states", required = false) String states,
                                            @RequestParam(name = "excludePropertyTerms", required = false) String excludePropertyTerms,
                                            @RequestParam(name = "excludeTopLevelAbieIds", required = false) String excludeTopLevelAbieIds,
                                            @RequestParam(name = "ownerLoginIds", required = false) String ownerLoginIds,
                                            @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
                                            @RequestParam(name = "updateStart", required = false) String updateStart,
                                            @RequestParam(name = "updateEnd", required = false) String updateEnd,
                                            @RequestParam(name = "releaseId", required = false) BigInteger releaseId,
                                            @RequestParam(name = "sortActive") String sortActive,
                                            @RequestParam(name = "sortDirection") String sortDirection,
                                            @RequestParam(name = "pageIndex") int pageIndex,
                                            @RequestParam(name = "pageSize") int pageSize) {

        BieListRequest request = new BieListRequest();

        request.setPropertyTerm(propertyTerm);
        request.setBusinessContext(businessContext);
        request.setAccManifestId(accManifestId);
        request.setAccess(!StringUtils.isEmpty(access) ? AccessPrivilege.valueOf(access) : null);
        request.setStates(!StringUtils.isEmpty(states) ?
                Arrays.asList(states.split(",")).stream()
                        .map(e -> BieState.valueOf(e)).collect(Collectors.toList()) : Collections.emptyList());
        request.setExcludePropertyTerms(StringUtils.isEmpty(excludePropertyTerms) ? Collections.emptyList() :
                Arrays.asList(excludePropertyTerms.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).collect(Collectors.toList()));
        request.setExcludeTopLevelAbieIds(StringUtils.isEmpty(excludeTopLevelAbieIds) ? Collections.emptyList() :
                Arrays.asList(excludeTopLevelAbieIds.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).map(e -> new BigInteger(e)).collect(Collectors.toList()));
        request.setOwnerLoginIds(StringUtils.isEmpty(ownerLoginIds) ? Collections.emptyList() :
                Arrays.asList(ownerLoginIds.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).collect(Collectors.toList()));
        request.setUpdaterLoginIds(StringUtils.isEmpty(updaterLoginIds) ? Collections.emptyList() :
                Arrays.asList(updaterLoginIds.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).collect(Collectors.toList()));

        request.setReleaseId(releaseId);

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
        return service.getBieList(user, request);
    }

    @RequestMapping(value = "/profile_bie_list/delete", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteBieList(@AuthenticationPrincipal User user,
                                        @RequestBody DeleteBieListRequest request) {
        List<BigInteger> topLevelAbieIds = request.getTopLevelAbieIds();
        service.deleteBieList(user, topLevelAbieIds);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/profile_bie/business_ctx_from_abie/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BizCtx findBizCtxFromAbieId(@PathVariable("id") BigInteger abieId) {
        return service.findBizCtxByAbieId(abieId);
    }

    @RequestMapping(value = "/profile_bie/{id}/biz_ctx", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BizCtxAssignment> getAssignBizCtx(@PathVariable("id") BigInteger topLevelAbieId) {
        return service.getAssignBizCtx(topLevelAbieId);
    }

    @RequestMapping(value = "/profile_bie/{id}/assign_biz_ctx", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity assignBizCtx(@AuthenticationPrincipal User user,
                                       @PathVariable("id") BigInteger topLevelAbieId,
                                       @RequestBody Map<String, List<Long>> request) {
        service.assignBizCtx(user, topLevelAbieId, request.getOrDefault("bizCtxList", Collections.emptyList()));
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/profile_bie/{id}/transfer_ownership", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity transferOwnership(@AuthenticationPrincipal User user,
                                            @PathVariable("id") BigInteger topLevelAbieId,
                                            @RequestBody Map<String, String> request) {
        String targetLoginId = request.get("targetLoginId");
        service.transferOwnership(user, topLevelAbieId, targetLoginId);
        return ResponseEntity.noContent().build();
    }

}
