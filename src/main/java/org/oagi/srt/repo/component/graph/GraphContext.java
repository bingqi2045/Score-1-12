package org.oagi.srt.repo.component.graph;

import org.oagi.srt.gateway.http.api.graph.Node;

import java.util.List;

public interface GraphContext {

    public List<Node> findChildren(Node node);

}
