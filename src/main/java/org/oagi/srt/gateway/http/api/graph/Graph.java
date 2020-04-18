package org.oagi.srt.gateway.http.api.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.jooq.DSLContext;
import org.jooq.tools.StringUtils;
import org.jooq.types.ULong;

import javax.xml.bind.annotation.XmlTransient;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class Graph {

    @XmlTransient
    @JsonIgnore
    private transient DSLContext dslContext;

    @XmlTransient
    @JsonIgnore
    private transient Map<Node.NodeType, List<ULong>> nodeManifestIds = new HashMap();

    private Map<String, Node> nodes = new LinkedHashMap();
    private Map<String, Edge> edges = new LinkedHashMap();

    public Graph(DSLContext dslContext) {
        this.dslContext = dslContext;

        Arrays.asList(Node.NodeType.values()).stream().forEach(e -> {
            nodeManifestIds.put(e, new ArrayList());
        });
    }

    public boolean addNode(Node node) {
        String key = node.getKey();
        if (nodes.containsKey(key)) {
            return false;
        }

        nodes.put(key, node);
        nodeManifestIds.get(node.getType()).add(node.getManifestId());

        return true;
    }

    public void addEdges(Node source, List<Node> children) {
        String sourceKey = source.getKey();
        if (edges.containsKey(sourceKey)) {
            return;
        }
        Edge edge = new Edge();
        children.stream().forEach(e -> {
            edge.addTarget(e.getKey());
        });
        edges.put(sourceKey, edge);
    }

    public Collection<List<String>> findPaths(String from, String query) {
        if (StringUtils.isEmpty(query)) {
            return Collections.emptyList();
        }

        Node root = nodes.get(from);
        if (root == null) {
            return Collections.emptyList();
        }

        String qlc = query.toLowerCase().trim();
        LinkedList<List<Node>> linkedList = new LinkedList();
        linkedList.add(Arrays.asList(root));

        Set<List<String>> paths = new LinkedHashSet();

        while (!linkedList.isEmpty()) {
            List<Node> cur = linkedList.poll();
            Node lastNode = cur.get(cur.size() - 1);
            if (lastNode.hasTerm(qlc) && !lastNode.isAssociation()) {
                paths.add(cur.stream().map(e -> e.getKey()).collect(Collectors.toList()));
            }

            List<String> targets = edges.getOrDefault(lastNode.getKey(), Edge.EMPTY_EDGE).getTargets();
            if (!targets.isEmpty()) {
                linkedList.addAll(linkedList.indexOf(cur) + 1, targets.stream().map(e -> {
                    List<Node> n = new ArrayList(cur);
                    n.add(nodes.get(e));
                    return n;
                }).collect(Collectors.toList()));
            }
        }

        return paths;
    }

}
