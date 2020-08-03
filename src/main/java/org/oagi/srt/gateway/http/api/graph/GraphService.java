package org.oagi.srt.gateway.http.api.graph;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.AccManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.AsccpManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.BccpManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.CodeListManifestRecord;
import org.oagi.srt.repo.BusinessInformationEntityRepository;
import org.oagi.srt.repo.CoreComponentRepository;
import org.oagi.srt.repo.component.code_list.CodeListReadRepository;
import org.oagi.srt.repo.component.graph.CodeListGraphContext;
import org.oagi.srt.repo.component.graph.CoreComponentGraphContext;
import org.oagi.srt.repo.component.graph.GraphContext;
import org.oagi.srt.repo.component.graph.GraphContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collections;
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
    private CodeListReadRepository codeListReadRepository;

    @Autowired
    private DSLContext dslContext;

    public Graph getAccGraph(BigInteger accManifestId) {
        AccManifestRecord accManifest =
                coreComponentRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        if (accManifest == null) {
            throw new IllegalArgumentException();
        }

        CoreComponentGraphContext coreComponentGraphContext =
                graphContextRepository.buildGraphContext(accManifest);
        return buildGraph(coreComponentGraphContext, coreComponentGraphContext.toNode(accManifest));
    }

    public Graph getAsccpGraph(BigInteger asccpManifestId) {
        AsccpManifestRecord asccpManifest =
                coreComponentRepository.getAsccpManifestByManifestId(ULong.valueOf(asccpManifestId));
        if (asccpManifest == null) {
            throw new IllegalArgumentException();
        }

        CoreComponentGraphContext coreComponentGraphContext =
                graphContextRepository.buildGraphContext(asccpManifest);
        return buildGraph(coreComponentGraphContext, coreComponentGraphContext.toNode(asccpManifest));
    }

    public Graph getBccpGraph(BigInteger bccpManifestId) {
        BccpManifestRecord bccpManifest =
                coreComponentRepository.getBccpManifestByManifestId(ULong.valueOf(bccpManifestId));
        if (bccpManifest == null) {
            throw new IllegalArgumentException();
        }

        CoreComponentGraphContext coreComponentGraphContext =
                graphContextRepository.buildGraphContext(bccpManifest);
        return buildGraph(coreComponentGraphContext, coreComponentGraphContext.toNode(bccpManifest));
    }

    public Graph getBieGraph(BigInteger topLevelAsbiepId) {
        BigInteger asccpManifestId = bieRepository.getAsccpManifestIdByTopLevelAsbiepId(topLevelAsbiepId);
        return getAsccpGraph(asccpManifestId);
    }

    public Graph getExtensionGraph(BigInteger extensionAccManifestId) {
        Graph extensionAccGraph = getAccGraph(extensionAccManifestId);
        Node extensionNode = extensionAccGraph.getNode(Node.NodeType.ACC, extensionAccManifestId);
        String objectClassTerm = (String) extensionNode.getProperties().get("objectClassTerm");

        // !isGlobalExtension
        if (!"All Extension".equals(objectClassTerm)) {
            BigInteger globalExtensionAccManifestId =
                    coreComponentRepository.getGlobalExtensionAccManifestId(extensionAccManifestId);
            Graph globalExtensionAccGraph = getAccGraph(globalExtensionAccManifestId);
            extensionAccGraph.merge(globalExtensionAccGraph);

            Node globalExtensionNode =
                    globalExtensionAccGraph.getNode(Node.NodeType.ACC, globalExtensionAccManifestId);

            Edge extensionEdge = extensionAccGraph.addEdges(extensionNode, Collections.emptyList());
            extensionEdge.addTarget(0, globalExtensionNode.getKey());
        }

        return extensionAccGraph;
    }

    public Graph getCodeListGraph(BigInteger codeListManifestId) {
        CodeListManifestRecord codeListManifestRecord =
                codeListReadRepository.getCodeListManifestByManifestId(codeListManifestId);
        if (codeListManifestRecord == null) {
            throw new IllegalArgumentException();
        }

        CodeListGraphContext codeListGraphContext =
                graphContextRepository.buildGraphContext(codeListManifestRecord);
        return buildGraph(codeListGraphContext, codeListGraphContext.toNode(codeListManifestRecord));
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
