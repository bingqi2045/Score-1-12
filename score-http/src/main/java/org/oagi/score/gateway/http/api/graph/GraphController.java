package org.oagi.score.gateway.http.api.graph;

import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class GraphController {

    @Autowired
    private GraphService graphService;

    @RequestMapping(value = "/graphs/{type}/{id:[\\d]+}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getGraph(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                        @PathVariable("type") String type,
                                        @PathVariable("id") BigInteger id,
                                        @RequestParam(value = "q", required = false) String query) {
        Graph graph;
        switch (type.toLowerCase()) {
            case "acc":
                graph = graphService.getAccGraph(id);
                break;

            case "asccp":
                graph = graphService.getAsccpGraph(id, false);
                break;

            case "bccp":
                graph = graphService.getBccpGraph(id);
                break;

            case "abie":
                graph = graphService.getBieGraph(user, id);
                break;

            // This is only for UI to draw 'All Extension' as a based ACC.
            case "extension":
                graph = graphService.getExtensionGraph(id);
                break;

            case "code_list":
                graph = graphService.getCodeListGraph(id);
                break;

            default:
                throw new UnsupportedOperationException();
        }

        Map<String, Object> response = new HashMap();

        if (!StringUtils.isEmpty(query)) {
            Collection<List<String>> paths = graph.findPaths(type + id, query);
            response.put("query", query);
            response.put("paths", paths.stream()
                    .map(e -> e.stream()
                            .filter(item -> !item.matches("ascc\\d+|bcc\\d+|bdt\\d+"))
                            .collect(Collectors.joining(">"))
                    )
                    .collect(Collectors.toList()));
        } else {
            response.put("graph", graph);
        }

        return response;
    }
}
