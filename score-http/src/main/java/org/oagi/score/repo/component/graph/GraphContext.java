package org.oagi.score.repo.component.graph;

import org.oagi.score.gateway.http.api.graph.Node;

import java.util.List;

public interface GraphContext {

    public List<Node> findChildren(Node node, boolean excludeUEG);

}
