package org.oagi.srt.gateway.http.api.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.DSLContext;
import org.jooq.tools.StringUtils;
import org.jooq.types.ULong;

import javax.xml.bind.annotation.XmlTransient;
import java.util.*;
import java.util.stream.Collectors;

import static org.oagi.srt.entity.jooq.Tables.*;

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

    public void build() {
        nodeManifestIds.entrySet().stream().forEach(entry -> {
            switch (entry.getKey()) {
                case ACC:
                    dslContext.select(ACC_MANIFEST.ACC_MANIFEST_ID, ACC.OBJECT_CLASS_TERM)
                            .from(ACC)
                            .join(ACC_MANIFEST).on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                            .where(ACC_MANIFEST.ACC_MANIFEST_ID.in(entry.getValue()))
                            .fetchStream().forEach(record -> {
                        Node node = nodes.get(Node.NodeType.ACC.toString() + record.value1());
                        node.put("objectClassTerm", record.value2());
                    });
                    break;

                case ASCCP:
                    dslContext.select(ASCCP_MANIFEST.ASCCP_MANIFEST_ID, ASCCP.PROPERTY_TERM)
                            .from(ASCCP)
                            .join(ASCCP_MANIFEST).on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                            .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.in(entry.getValue()))
                            .fetchStream().forEach(record -> {
                        Node node = nodes.get(Node.NodeType.ASCCP.toString() + record.value1());
                        node.put("propertyTerm", record.value2());
                    });
                    break;

                case BCCP:
                    dslContext.select(BCCP_MANIFEST.BCCP_MANIFEST_ID, BCCP.PROPERTY_TERM, BCCP.REPRESENTATION_TERM)
                            .from(BCCP)
                            .join(BCCP_MANIFEST).on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                            .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.in(entry.getValue()))
                            .fetchStream().forEach(record -> {
                        Node node = nodes.get(Node.NodeType.BCCP.toString() + record.value1());
                        node.put("propertyTerm", record.value2());
                        node.put("representationTerm", record.value3());
                    });
                    break;

                case BDT:
                    dslContext.select(DT_MANIFEST.DT_MANIFEST_ID, DT.DATA_TYPE_TERM, DT.QUALIFIER)
                            .from(DT)
                            .join(DT_MANIFEST).on(DT.DT_ID.eq(DT_MANIFEST.DT_ID))
                            .where(DT_MANIFEST.DT_MANIFEST_ID.in(entry.getValue()))
                            .fetchStream().forEach(record -> {
                        Node node = nodes.get(Node.NodeType.BDT.toString() + record.value1());
                        node.put("dataTypeTerm", record.value2());
                        String qualifier = record.value3();
                        if (!StringUtils.isEmpty(qualifier)) {
                            node.put("qualifier", qualifier);
                        }
                    });
                    break;

                case BDTSC:
                    dslContext.select(DT_SC_MANIFEST.DT_SC_MANIFEST_ID, DT_SC.PROPERTY_TERM, DT_SC.REPRESENTATION_TERM)
                            .from(DT_SC)
                            .join(DT_SC_MANIFEST).on(DT_SC.DT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                            .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.in(entry.getValue()))
                            .fetchStream().forEach(record -> {
                        Node node = nodes.get(Node.NodeType.BDTSC.toString() + record.value1());
                        node.put("propertyTerm", record.value2());
                        node.put("representationTerm", record.value3());
                    });
                    break;
            }
        });
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
        Stack<List<Node>> stack = new Stack();
        stack.push(Arrays.asList(root));

        Set<List<String>> paths = new LinkedHashSet();

        while (!stack.isEmpty()) {
            List<Node> cur = stack.pop();
            Node lastNode = cur.get(cur.size() - 1);
            if (lastNode.hasTerm(qlc)) {
                paths.add(cur.stream().map(e -> e.getKey()).collect(Collectors.toList()));
            }

            List<String> targets = edges.getOrDefault(lastNode.getKey(), Edge.EMPTY_EDGE).getTargets();
            if (!targets.isEmpty()) {
                stack.addAll(targets.stream().map(e -> {
                    List<Node> n = new ArrayList(cur);
                    n.add(nodes.get(e));
                    return n;
                }).collect(Collectors.toList()));
            }
        }

        return paths;
    }

}
