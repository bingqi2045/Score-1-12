package org.oagi.score.gateway.http.api.agency_id_management.controller;

import org.oagi.score.gateway.http.api.agency_id_management.data.SimpleAgencyIdListValue;
import org.oagi.score.gateway.http.api.agency_id_management.service.AgencyIdService;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.repo.api.agency.model.AgencyIdList;
import org.oagi.score.repo.api.agency.model.GetAgencyIdListListRequest;
import org.oagi.score.repo.api.agency.model.GetAgencyIdListListResponse;
import org.oagi.score.service.common.data.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
public class AgencyIdController {

    @Autowired
    private AgencyIdService service;

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/simple_agency_id_list_values", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SimpleAgencyIdListValue> getSimpleAgencyIdListValues() {
        return service.getSimpleAgencyIdListValues();
    }

    @RequestMapping(value = "/agency_id_list/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AgencyIdList getAgencyIdListDetail(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                              @PathVariable("id") BigInteger manifestId) {
        return service.getAgencyIdListDetail(manifestId);
    }

    @RequestMapping(value = "/agency_id_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GetAgencyIdListListResponse getAgencyIdListList(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                                           @RequestParam(name = "states", required = false) String states,
                                                           @RequestParam(name = "ownerLoginIds", required = false) String ownerLoginIds,
                                                           @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
                                                           @RequestParam(name = "updateStart", required = false) String updateStart,
                                                           @RequestParam(name = "updateEnd", required = false) String updateEnd,
                                                           @RequestParam(name = "releaseId", required = false) BigInteger releaseId,
                                                           @RequestParam(name = "sortActive") String sortActive,
                                                           @RequestParam(name = "sortDirection") String sortDirection,
                                                           @RequestParam(name = "pageIndex") int pageIndex,
                                                           @RequestParam(name = "pageSize") int pageSize) {
        GetAgencyIdListListRequest request = new GetAgencyIdListListRequest(sessionService.asScoreUser(user));
        request.setReleaseId(releaseId);
        return service.getAgencyIdListList(request);
    }

}
