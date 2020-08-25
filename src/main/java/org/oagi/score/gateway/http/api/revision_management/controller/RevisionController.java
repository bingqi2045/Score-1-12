package org.oagi.score.gateway.http.api.revision_management.controller;

import org.jooq.tools.StringUtils;
import org.oagi.score.gateway.http.api.common.data.PageRequest;
import org.oagi.score.gateway.http.api.common.data.PageResponse;
import org.oagi.score.gateway.http.api.revision_management.data.Revision;
import org.oagi.score.gateway.http.api.revision_management.data.RevisionListRequest;
import org.oagi.score.gateway.http.api.revision_management.service.RevisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
public class RevisionController {

    @Autowired
    private RevisionService service;

    @RequestMapping(value = "/revisions/{reference}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<Revision> getRevisions(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                               @PathVariable("reference") String reference,
                                               @RequestParam(name = "sortActive") String sortActive,
                                               @RequestParam(name = "sortDirection") String sortDirection,
                                               @RequestParam(name = "pageIndex") int pageIndex,
                                               @RequestParam(name = "pageSize") int pageSize) {
        if (StringUtils.isEmpty(reference)) {
            throw new IllegalArgumentException("Unknown reference");
        }

        RevisionListRequest request = new RevisionListRequest();
        request.setReference(reference);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setSortActive(sortActive);
        pageRequest.setSortDirection(sortDirection);
        pageRequest.setPageIndex(pageIndex);
        pageRequest.setPageSize(pageSize);
        request.setPageRequest(pageRequest);

        return service.getRevisionByReference(request);
    }

    @RequestMapping(value = "/revisions/{revisionId}/snapshot", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getSnapshot(@AuthenticationPrincipal AuthenticatedPrincipal user,
                              @PathVariable("revisionId") BigInteger revisionId) {
        return service.getSnapshotById(user, revisionId);
    }
}
