package org.oagi.srt.gateway.http.api.bie_management.controller;

import org.oagi.srt.gateway.http.api.bie_management.data.BieCreateRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.BieCreateResponse;
import org.oagi.srt.gateway.http.api.bie_management.service.BieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BieCreateController {

    @Autowired
    private BieService bieService;

    @RequestMapping(value = "/profile_bie/create", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieCreateResponse create(
            @AuthenticationPrincipal User user,
            @RequestBody BieCreateRequest bieCreateRequest) {

        BieCreateResponse response = bieService.createBie(user, bieCreateRequest);
        return response;
    }

}
