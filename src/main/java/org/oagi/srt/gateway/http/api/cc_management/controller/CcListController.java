package org.oagi.srt.gateway.http.api.cc_management.controller;

import org.oagi.srt.data.ASCCP;
import org.oagi.srt.data.ACC;
import org.oagi.srt.data.BCC;
import org.oagi.srt.data.BCCP;
import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListStates;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListTypes;
import org.oagi.srt.gateway.http.api.cc_management.service.CcListService;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CcListController {

    @Autowired
    private CcListService service;

    @RequestMapping(value = "/core_component", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public PageResponse<CcList> getCcList(
            @RequestParam(name = "release_id", required = false) long releaseId,
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "types", required = false) String types,
            @RequestParam(name = "states", required = false) String states,
            @RequestParam(name = "sortActive") String sortActive,
            @RequestParam(name = "sortDirection") String sortDirection,
            @RequestParam(name = "pageIndex") int pageIndex,
            @RequestParam(name = "pageSize") int pageSize) {

        PageRequest pageRequest = new PageRequest();
        pageRequest.setSortActive(sortActive);
        pageRequest.setSortDirection(sortDirection);
        pageRequest.setPageIndex(pageIndex);
        pageRequest.setPageSize(pageSize);

        return service.getCcList(releaseId,
                CcListTypes.fromString(types),
                CcListStates.fromString(states),
                filter,
                pageRequest);
    }
    @RequestMapping(value = "/core_component/asccp/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ASCCP getAsccp(@PathVariable("id") long id) {
        return service.getAsccp(id);
    }

    @RequestMapping(value = "/core_component/acc/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ACC getAcc(@PathVariable("id") long id) {
        return service.getAcc(id);
    }

    @RequestMapping(value = "/core_component/bccp/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BCCP getBccp(@PathVariable("id") long id) {
        return service.getBccp(id);
    }

    @RequestMapping(value = "/core_component/bccs/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BCC> getBccs(@PathVariable("id") long id) {
        return service.getBccs(id);
    }
}
