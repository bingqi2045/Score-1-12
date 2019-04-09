package org.oagi.srt.gateway.http.api.bie_management.controller;

import org.oagi.srt.data.BieState;
import org.oagi.srt.gateway.http.api.bie_management.data.BieList;
import org.oagi.srt.gateway.http.api.bie_management.data.BieListRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.DeleteBieListRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.GetBieListRequest;
import org.oagi.srt.gateway.http.api.bie_management.service.BieService;
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

@RestController
public class BieListController {

    @Autowired
    private BieService service;

    @RequestMapping(value = "/bie_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public PageResponse<BieList> getBieList(@AuthenticationPrincipal User user,
                                            @RequestParam(name = "name", required = false) String name,
                                            @RequestParam(name = "states", required = false) String states,
                                            @RequestParam(name = "ownerLoginIds", required = false) String ownerLoginIds,
                                            @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
                                            @RequestParam(name = "updateStart", required = false) String updateStart,
                                            @RequestParam(name = "updateEnd", required = false) String updateEnd,
                                            @RequestParam(name = "sortActive") String sortActive,
                                            @RequestParam(name = "sortDirection") String sortDirection,
                                            @RequestParam(name = "pageIndex") int pageIndex,
                                            @RequestParam(name = "pageSize") int pageSize) {

        BieListRequest request = new BieListRequest();

        request.setName(name);
        request.setStates(!StringUtils.isEmpty(states) ?
                Arrays.asList(states.split(",")).stream()
                        .map(e -> BieState.valueOf(e)).collect(Collectors.toList()) : Collections.emptyList());

        request.setOwnerLoginIds(StringUtils.isEmpty(ownerLoginIds) ?
                Collections.emptyList() : Arrays.asList(ownerLoginIds.split(",")));
        request.setUpdaterLoginIds(StringUtils.isEmpty(updaterLoginIds) ?
                Collections.emptyList() : Arrays.asList(updaterLoginIds.split(",")));

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

    @RequestMapping(value = "/profile_bie_list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BieList> getBieList(@AuthenticationPrincipal User user,
                                    @RequestParam(value = "biz_ctx_id", required = false) Long bizCtxId,
                                    @RequestParam(value = "exclude_json_related", required = false) Boolean excludeJsonRelated) {
        return service.getBieList(new GetBieListRequest(user, bizCtxId, excludeJsonRelated));
    }

    @RequestMapping(value = "/profile_bie_list/meta_header", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BieList> getMetaHeaderBieList(@AuthenticationPrincipal User user) {
        return service.getMetaHeaderBieList(user);
    }

    @RequestMapping(value = "/profile_bie_list/pagination_response", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BieList> getPaginationResponseBieList(@AuthenticationPrincipal User user) {
        return service.getPaginationResponseBieList(user);
    }

    @RequestMapping(value = "/profile_bie_list/delete", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteBieList(@RequestBody DeleteBieListRequest request) {
        List<Long> topLevelAbieIds = request.getTopLevelAbieIds();
        if (!topLevelAbieIds.isEmpty()) {
            service.deleteBieList(topLevelAbieIds);
        }
        return ResponseEntity.noContent().build();
    }
}
