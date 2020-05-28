package org.oagi.srt.gateway.http.api.graph;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.AccManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.AsccpManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.BccpManifestRecord;
import org.oagi.srt.repo.BusinessInformationEntityRepository;
import org.oagi.srt.repo.CoreComponentRepository;
import org.oagi.srt.repo.GraphContext;
import org.oagi.srt.repo.GraphContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
@Transactional(readOnly = true)
public class GraphService {

    @Autowired
    private CoreComponentRepository coreComponentRepository;

    @Autowired
    private GraphContextRepository graphContextRepository;

    @Autowired
    private BusinessInformationEntityRepository bieRepository;

    @Autowired
    private DSLContext dslContext;

    public Graph getAccGraph(BigInteger accManifestId) {
        AccManifestRecord accManifest =
                coreComponentRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        if (accManifest == null) {
            throw new IllegalArgumentException();
        }

        GraphContext graphContext =
                graphContextRepository.buildGraphContext(accManifest);
        return buildGraph(graphContext, graphContext.toNode(accManifest));
    }

    public Graph getAsccpGraph(BigInteger asccpManifestId) {
        AsccpManifestRecord asccpManifest =
                coreComponentRepository.getAsccpManifestByManifestId(ULong.valueOf(asccpManifestId));
        if (asccpManifest == null) {
            throw new IllegalArgumentException();
        }

        GraphContext graphContext =
                graphContextRepository.buildGraphContext(asccpManifest);
        return buildGraph(graphContext, graphContext.toNode(asccpManifest));
    }

    public Graph getBccpGraph(BigInteger bccpManifestId) {
        BccpManifestRecord bccpManifest =
                coreComponentRepository.getBccpManifestByManifestId(ULong.valueOf(bccpManifestId));
        if (bccpManifest == null) {
            throw new IllegalArgumentException();
        }

        GraphContext graphContext =
                graphContextRepository.buildGraphContext(bccpManifest);
        return buildGraph(graphContext, graphContext.toNode(bccpManifest));
    }

    public Graph getBieGraph(BigInteger topLevelAbieId) {
        BigInteger asccpManifestId = bieRepository.getAsccpManifestIdByTopLevelAbieId(topLevelAbieId);
        return getAsccpGraph(asccpManifestId);
    }

    private Graph buildGraph(GraphContext graphContext, Node root) {
        Queue<Node> manifestQueue = new LinkedList<>();
        manifestQueue.add(root);

        Graph graph = new Graph(dslContext);

        while (!manifestQueue.isEmpty()) {
            Node node = manifestQueue.poll();
            if (!graph.addNode(node)) {
                continue;
            }

            List<Node> children = graphContext.findChildren(node);
            if (children.isEmpty()) {
                continue;
            }

            graph.addEdges(node, children);
            if (Node.NodeType.BDT == node.getType()) {
                children.stream().forEach(e -> graph.addNode(e));
            } else {
                manifestQueue.addAll(children);
            }
        }

        return graph;
    }

}
