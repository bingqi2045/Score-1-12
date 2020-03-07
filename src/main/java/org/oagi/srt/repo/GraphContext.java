package org.oagi.srt.repo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.graph.Node;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Data
public class GraphContext {

    private DSLContext dslContext;

    private Map<ULong, AccManifestRecord> accManifestMap;
    private Map<ULong, AsccpManifestRecord> asccpManifestMap;
    private Map<ULong, BccpManifestRecord> bccpManifestMap;
    private Map<ULong, List<AsccManifest>> asccManifestMap;
    private Map<ULong, List<BccManifest>> bccManifestMap;
    private Map<ULong, DtManifestRecord> dtManifestMap;
    private Map<ULong, List<DtScManifestRecord>> dtScManifestMap;

    private interface Assoc {
        int getSeqKey();
    }

    @Data
    @AllArgsConstructor
    public class AsccManifest implements Assoc {
        private ULong asccManifestId;
        private ULong fromAccManifestId;
        private ULong toAsccpManifestId;
        private int seqKey;
    }

    @Data
    @AllArgsConstructor
    public class BccManifest implements Assoc {
        private ULong bccManifestId;
        private ULong fromAccManifestId;
        private ULong toBccpManifestId;
        private int seqKey;
    }

    public GraphContext(DSLContext dslContext, ULong releaseId) {
        this.dslContext = dslContext;

        accManifestMap =
                dslContext.selectFrom(ACC_MANIFEST)
                        .where(ACC_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetchStream().collect(Collectors.toMap(AccManifestRecord::getAccManifestId, Function.identity()));
        asccpManifestMap =
                dslContext.selectFrom(ASCCP_MANIFEST)
                        .where(ASCCP_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetchStream().collect(Collectors.toMap(AsccpManifestRecord::getAsccpManifestId, Function.identity()));
        bccpManifestMap =
                dslContext.selectFrom(BCCP_MANIFEST)
                        .where(BCCP_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetchStream().collect(Collectors.toMap(BccpManifestRecord::getBccpManifestId, Function.identity()));
        asccManifestMap =
                dslContext.select(
                        ASCC_MANIFEST.ASCC_MANIFEST_ID,
                        ASCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                        ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID,
                        ASCC.SEQ_KEY)
                        .from(ASCC_MANIFEST)
                        .join(ASCC).on(ASCC_MANIFEST.ASCC_ID.eq(ASCC.ASCC_ID))
                        .where(ASCC_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetch(record -> new AsccManifest(
                                record.get(ASCC_MANIFEST.ASCC_MANIFEST_ID),
                                record.get(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID),
                                record.get(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID),
                                record.get(ASCC.SEQ_KEY)
                        )).stream()
                        .collect(groupingBy(AsccManifest::getFromAccManifestId));
        bccManifestMap =
                dslContext.select(
                        BCC_MANIFEST.BCC_MANIFEST_ID,
                        BCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                        BCC_MANIFEST.TO_BCCP_MANIFEST_ID,
                        BCC.SEQ_KEY)
                        .from(BCC_MANIFEST)
                        .join(BCC).on(BCC_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                        .where(BCC_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetch(record -> new BccManifest(
                                record.get(BCC_MANIFEST.BCC_MANIFEST_ID),
                                record.get(BCC_MANIFEST.FROM_ACC_MANIFEST_ID),
                                record.get(BCC_MANIFEST.TO_BCCP_MANIFEST_ID),
                                record.get(BCC.SEQ_KEY)
                        )).stream()
                        .collect(groupingBy(BccManifest::getFromAccManifestId));
        dtManifestMap =
                dslContext.selectFrom(DT_MANIFEST)
                        .where(DT_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetchStream().collect(Collectors.toMap(DtManifestRecord::getDtManifestId, Function.identity()));
        dtScManifestMap =
                dslContext.select(DT_SC_MANIFEST.fields())
                        .from(DT_SC_MANIFEST)
                        .join(DT_SC).on(DT_SC_MANIFEST.DT_SC_ID.eq(DT_SC.DT_SC_ID))
                        .where(and(
                                DT_SC_MANIFEST.RELEASE_ID.eq(releaseId),
                                DT_SC.CARDINALITY_MAX.ne(0)
                        ))
                        .fetchInto(DtScManifestRecord.class).stream()
                        .collect(groupingBy(DtScManifestRecord::getOwnerDtManifestId));
    }

    public List<Node> findChildren(Node node) {
        switch (node.getType()) {
            case ACC:
                List<Node> children = new ArrayList();
                if (node.getBasedManifestId() != null) {
                    AccManifestRecord base = accManifestMap.get(node.getBasedManifestId());
                    children.add(Node.toNode(base));
                }

                List<Assoc> assocs = new ArrayList();
                assocs.addAll(asccManifestMap.getOrDefault(node.getManifestId(), Collections.emptyList()));
                assocs.addAll(bccManifestMap.getOrDefault(node.getManifestId(), Collections.emptyList()));
                Collections.sort(assocs, Comparator.comparing(Assoc::getSeqKey));
                children.addAll(
                        assocs.stream().map(e -> {
                            if (e instanceof AsccManifest) {
                                return Node.toNode(asccpManifestMap.get(((AsccManifest) e).getToAsccpManifestId()));
                            } else {
                                return Node.toNode(bccpManifestMap.get(((BccManifest) e).getToBccpManifestId()));
                            }
                        }).collect(Collectors.toList())
                );

                return children;

            case ASCCP:
                AccManifestRecord accManifest = accManifestMap.get(node.getLinkedManifestId());
                return (accManifest != null) ? Arrays.asList(Node.toNode(accManifest)) : Collections.emptyList();

            case BCCP:
                DtManifestRecord dtManifest = dtManifestMap.get(node.getLinkedManifestId());
                return (dtManifest != null) ? Arrays.asList(Node.toNode(dtManifest)) : Collections.emptyList();

            case BDT:
                return dtScManifestMap.getOrDefault(node.getManifestId(), Collections.emptyList()).stream()
                        .map(e -> Node.toNode(e)).collect(Collectors.toList());

            case BDT_SC:
            default:
                return Collections.emptyList();
        }
    }
}
