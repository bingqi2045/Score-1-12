package org.oagi.srt.gateway.http.api.graph;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.AccManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.AsccpManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.BccpManifestRecord;
import org.oagi.srt.repo.CoreComponentRepository;
import org.oagi.srt.repo.GraphContext;
import org.oagi.srt.repo.GraphContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.Queue;

@Service
@Transactional(readOnly = true)
public class GraphService {

    @Autowired
    private CoreComponentRepository coreComponentRepository;

    @Autowired
    private GraphContextRepository graphContextRepository;

    @Autowired
    private DSLContext dslContext;

    public Graph getAccGraph(long accManifestId) {
        AccManifestRecord accManifest =
                coreComponentRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        if (accManifest == null) {
            throw new IllegalArgumentException();
        }

        GraphContext graphContext =
                graphContextRepository.buildGraphContext(accManifest);
        return buildGraph(graphContext, accManifest);
    }

    public Graph getAsccpGraph(long asccpManifestId) {
        AsccpManifestRecord asccpManifest =
                coreComponentRepository.getAsccpManifestByManifestId(ULong.valueOf(asccpManifestId));
        if (asccpManifest == null) {
            throw new IllegalArgumentException();
        }

        GraphContext graphContext =
                graphContextRepository.buildGraphContext(asccpManifest);
        return buildGraph(graphContext, asccpManifest);
    }

    public Graph getBccpGraph(long bccpManifestId) {
        BccpManifestRecord bccpManifest =
                coreComponentRepository.getBccpManifestByManifestId(ULong.valueOf(bccpManifestId));
        if (bccpManifest == null) {
            throw new IllegalArgumentException();
        }

        GraphContext graphContext =
                graphContextRepository.buildGraphContext(bccpManifest);
        return buildGraph(graphContext, bccpManifest);
    }

    private Graph buildGraph(GraphContext graphContext, Object root) {
        Queue manifestQueue = new LinkedList<>();
        manifestQueue.add(root);

        Graph graph = new Graph(dslContext);

        while (!manifestQueue.isEmpty()) {
            Object node = manifestQueue.poll();
            graph.addNode(node);

            graphContext.findChildren(node).stream().forEach(child -> {
                graph.addEdge(node, child);
                manifestQueue.offer(child);
            });
        }

        graph.build();
        return graph;
    }

}
