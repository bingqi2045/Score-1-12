package org.oagi.srt.gateway.http.api.graph;

import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class GraphController {

    @Autowired
    private GraphService graphService;

    @RequestMapping(value = "/graphs/{type}/{manifestId:[\\d]+}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getGraph(@AuthenticationPrincipal User user,
                                        @PathVariable("type") String type,
                                        @PathVariable("manifestId") long manifestId,
                                        @RequestParam(value = "q", required = false) String query) {
        Graph graph;
        switch (type) {
            case "acc":
                graph = graphService.getAccGraph(manifestId);
                break;

            case "asccp":
                graph = graphService.getAsccpGraph(manifestId);
                break;

            case "bccp":
                graph = graphService.getBccpGraph(manifestId);
                break;

            default:
                throw new UnsupportedOperationException();
        }

        Map<String, Object> response = new HashMap();

        if (!StringUtils.isEmpty(query)) {
            Collection<List<String>> paths = graph.findPaths(type + manifestId, query);
            response.put("query", query);
            response.put("paths", paths.stream()
                    .map(e -> String.join(">", e))
                    .collect(Collectors.toList()));
        } else {
            response.put("graph", graph);
        }

        return response;
    }
}
