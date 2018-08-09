package org.oagi.srt.gateway.http.api.bie_management.controller;

import org.oagi.srt.gateway.http.api.bie_management.data.BieCopyRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.BieCopyResponse;
import org.oagi.srt.gateway.http.api.bie_management.service.BieCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BieCopyController {

    @Autowired
    private BieCopyService service;

    @RequestMapping(value = "/profile_bie/copy", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieCopyResponse create(
            @AuthenticationPrincipal User user,
            @RequestBody BieCopyRequest request) {

        BieCopyResponse response = service.copyBie(user, request);
        return response;
    }
}
