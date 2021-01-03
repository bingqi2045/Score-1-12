package org.oagi.score.gateway.http.api.bie_management.controller;

import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.service.bie.AnalysisBieUpliftingRequest;
import org.oagi.score.service.bie.AnalysisBieUpliftingResponse;
import org.oagi.score.service.bie.BieUpliftingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
public class BieUpliftingController {

    @Autowired
    private BieUpliftingService upliftingService;

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/profile_bie/{topLevelAsbiepId}/uplifting", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AnalysisBieUpliftingResponse analysisBieUplifting(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                                             @PathVariable("topLevelAsbiepId") BigInteger topLevelAsbiepId,
                                                             @RequestParam("targetReleaseId") BigInteger targetReleaseId) {

        AnalysisBieUpliftingRequest request = new AnalysisBieUpliftingRequest();
        request.setRequester(sessionService.asScoreUser(user));
        request.setTopLevelAsbiepId(topLevelAsbiepId);
        request.setTargetReleaseId(targetReleaseId);

        return upliftingService.analysisBieUplifting(request);
    }

}
