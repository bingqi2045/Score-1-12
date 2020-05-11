package org.oagi.srt.gateway.http.api.revision_management.controller;

import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.revision_management.data.Revision;
import org.oagi.srt.gateway.http.api.revision_management.data.RevisionListRequest;
import org.oagi.srt.gateway.http.api.revision_management.service.RevisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
public class RevisionController {

    @Autowired
    private RevisionService service;

    @RequestMapping(value = "/revisions/{reference}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<Revision> getRevisions(@AuthenticationPrincipal User user,
                                               @PathVariable("reference") String reference,
                                               @RequestParam(name = "pageIndex") int pageIndex,
                                               @RequestParam(name = "pageSize") int pageSize) {
        if (reference.isEmpty()) {
            throw new IllegalArgumentException("Unknown reference");
        }

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageIndex(pageIndex);
        pageRequest.setPageSize(pageSize);
        RevisionListRequest request = new RevisionListRequest();
        request.setReference(reference);
        request.setPageRequest(pageRequest);
        return service.getRevisionByReference(request);
    }
}
