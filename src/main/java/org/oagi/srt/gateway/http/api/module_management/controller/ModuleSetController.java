package org.oagi.srt.gateway.http.api.module_management.controller;

import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.module_management.data.ModuleSet;
import org.oagi.srt.gateway.http.api.module_management.data.ModuleSetListRequest;
import org.oagi.srt.gateway.http.api.module_management.service.ModuleSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
public class ModuleSetController {

    @Autowired
    private ModuleSetService service;

    @RequestMapping(value = "/module_set", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<ModuleSet> getNamespaceList(@AuthenticationPrincipal User user,
                                                    @RequestParam(name = "name", required = false) String name,
                                                    @RequestParam(name = "description", required = false) String description,
                                                    @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
                                                    @RequestParam(name = "updateStart", required = false) String updateStart,
                                                    @RequestParam(name = "updateEnd", required = false) String updateEnd,
                                                    @RequestParam(name = "sortActive") String sortActive,
                                                    @RequestParam(name = "sortDirection") String sortDirection,
                                                    @RequestParam(name = "pageIndex") int pageIndex,
                                                    @RequestParam(name = "pageSize") int pageSize) {
        ModuleSetListRequest request = new ModuleSetListRequest();

        request.setName(name);
        request.setDescription(description);
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
        return service.getModuleSetList(user, request);
    }

}
