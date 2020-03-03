package org.oagi.srt.gateway.http.api.graph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraphController {

    @Autowired
    private GraphService graphService;

    @RequestMapping(value = "/graphs/{type}/{manifestId:[\\d]+}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Graph getGraph(@AuthenticationPrincipal User user,
                          @PathVariable("type") String type,
                          @PathVariable("manifestId") long manifestId) {
        switch (type) {
            case "bccp":
                return graphService.getBccpGraph(manifestId);

            default:
                throw new UnsupportedOperationException();
        }
    }
}
