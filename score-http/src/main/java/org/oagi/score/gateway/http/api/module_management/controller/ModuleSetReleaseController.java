package org.oagi.score.gateway.http.api.module_management.controller;

import org.oagi.score.gateway.http.api.module_management.service.ModuleSetReleaseService;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.repo.api.module.model.GetModuleSetReleaseListRequest;
import org.oagi.score.repo.api.module.model.GetModuleSetReleaseListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.oagi.score.repo.api.base.SortDirection.ASC;
import static org.oagi.score.repo.api.base.SortDirection.DESC;

@RestController
public class ModuleSetReleaseController {

    @Autowired
    private ModuleSetReleaseService service;

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/module_set_release_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GetModuleSetReleaseListResponse getModuleSetReleaseList(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                                                   @RequestParam(name = "name", required = false) String name,
                                                                   @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
                                                                   @RequestParam(name = "updateStart", required = false) String updateStart,
                                                                   @RequestParam(name = "updateEnd", required = false) String updateEnd,
                                                                   @RequestParam(name = "sortActive") String sortActive,
                                                                   @RequestParam(name = "sortDirection") String sortDirection,
                                                                   @RequestParam(name = "pageIndex") int pageIndex,
                                                                   @RequestParam(name = "pageSize") int pageSize) {

        GetModuleSetReleaseListRequest request = new GetModuleSetReleaseListRequest(sessionService.asScoreUser(user));

        request.setName(name);
        request.setUpdaterUsernameList(!StringUtils.hasLength(updaterLoginIds) ? Collections.emptyList() :
                Arrays.asList(updaterLoginIds.split(","))
                        .stream().map(e -> e.trim()).filter(e -> StringUtils.hasLength(e))
                        .collect(Collectors.toList()));

        if (StringUtils.hasLength(updateStart)) {
            request.setUpdateStartDate(new Timestamp(Long.valueOf(updateStart)).toLocalDateTime());
        }
        if (StringUtils.hasLength(updateEnd)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(updateEnd));
            calendar.add(Calendar.DATE, 1);
            request.setUpdateEndDate(new Timestamp(calendar.getTimeInMillis()).toLocalDateTime());
        }

        request.setPageIndex(pageIndex);
        request.setPageSize(pageSize);
        request.setSortActive(sortActive);
        request.setSortDirection("asc".equalsIgnoreCase(sortDirection) ? ASC : DESC);

        return service.getModuleSetReleaseList(request);
    }
}
