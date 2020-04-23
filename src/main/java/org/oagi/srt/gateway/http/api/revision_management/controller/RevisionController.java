package org.oagi.srt.gateway.http.api.revision_management.controller;

import org.oagi.srt.gateway.http.api.revision_management.data.Revision;
import org.oagi.srt.gateway.http.api.revision_management.service.RevisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RevisionController {

    @Autowired
    private RevisionService service;

    @RequestMapping(value = "/revisions/{reference}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Revision> getRevisions(@AuthenticationPrincipal User user,
                                       @PathVariable("reference") String reference) {
        if (reference.isEmpty()) {
            throw new IllegalArgumentException("Unknown reference");
        }
        return service.getRevisionByReference(reference);
    }
}
