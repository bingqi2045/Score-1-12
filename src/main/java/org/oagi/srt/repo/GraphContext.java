package org.oagi.srt.repo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.DtManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.DtScManifestRecord;
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

    private Map<ULong, AccManifest> accManifestMap;
    private Map<ULong, AsccpManifest> asccpManifestMap;
    private Map<ULong, BccpManifest> bccpManifestMap;
    private Map<ULong, List<AsccManifest>> asccManifestMap;
    private Map<ULong, List<BccManifest>> bccManifestMap;
    private Map<ULong, DtManifestRecord> dtManifestMap;
    private Map<ULong, List<DtScManifestRecord>> dtScManifestMap;

    @Data
    @AllArgsConstructor
    public class AccManifest {
        private ULong accManifestId;
        private ULong basedAccManifestId;
        private String objectClassTerm;
    }

    @Data
    @AllArgsConstructor
    public class AsccpManifest {
        private ULong asccpManifestId;
        private ULong roleOfAccManifestId;
        private String propertyTerm;
    }

    @Data
    @AllArgsConstructor
    public class BccpManifest {
        private ULong bccpManifestId;
        private ULong bdtManifestId;
        private String propertyTerm;
        private String representationTerm;
    }

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
        private int cardinalityMin;
        private int cardinalityMax;
    }

    @Data
    @AllArgsConstructor
    public class BccManifest implements Assoc {
        private ULong bccManifestId;
        private ULong fromAccManifestId;
        private ULong toBccpManifestId;
        private int seqKey;
        private int cardinalityMin;
        private int cardinalityMax;
    }

    public GraphContext(DSLContext dslContext, ULong releaseId) {
        this.dslContext = dslContext;

        accManifestMap =
                dslContext.select(ACC_MANIFEST.ACC_MANIFEST_ID, ACC_MANIFEST.BASED_ACC_MANIFEST_ID, ACC.OBJECT_CLASS_TERM)
                        .from(ACC_MANIFEST)
                        .join(ACC).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                        .where(ACC_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetch(record -> new AccManifest(
                                record.get(ACC_MANIFEST.ACC_MANIFEST_ID),
                                record.get(ACC_MANIFEST.BASED_ACC_MANIFEST_ID),
                                record.get(ACC.OBJECT_CLASS_TERM)
                        )).stream()
                        .collect(Collectors.toMap(AccManifest::getAccManifestId, Function.identity()));
        asccpManifestMap =
                dslContext.select(ASCCP_MANIFEST.ASCCP_MANIFEST_ID, ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID, ASCCP.PROPERTY_TERM)
                        .from(ASCCP_MANIFEST)
                        .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                        .where(ASCCP_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetch(record -> new AsccpManifest(
                                record.get(ASCCP_MANIFEST.ASCCP_MANIFEST_ID),
                                record.get(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID),
                                record.get(ASCCP.PROPERTY_TERM)
                        )).stream()
                        .collect(Collectors.toMap(AsccpManifest::getAsccpManifestId, Function.identity()));
        bccpManifestMap =
                dslContext.select(BCCP_MANIFEST.BCCP_MANIFEST_ID, BCCP_MANIFEST.BDT_MANIFEST_ID, BCCP.PROPERTY_TERM, BCCP.REPRESENTATION_TERM)
                        .from(BCCP_MANIFEST)
                        .join(BCCP).on(BCCP_MANIFEST.BCCP_ID.eq(BCCP.BCCP_ID))
                        .where(BCCP_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetch(record -> new BccpManifest(
                                record.get(BCCP_MANIFEST.BCCP_MANIFEST_ID),
                                record.get(BCCP_MANIFEST.BDT_MANIFEST_ID),
                                record.get(BCCP.PROPERTY_TERM),
                                record.get(BCCP.REPRESENTATION_TERM)
                        )).stream()
                        .collect(Collectors.toMap(BccpManifest::getBccpManifestId, Function.identity()));
        asccManifestMap =
                dslContext.select(
                        ASCC_MANIFEST.ASCC_MANIFEST_ID,
                        ASCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                        ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID,
                        ASCC.SEQ_KEY,
                        ASCC.CARDINALITY_MIN,
                        ASCC.CARDINALITY_MAX)
                        .from(ASCC_MANIFEST)
                        .join(ASCC).on(ASCC_MANIFEST.ASCC_ID.eq(ASCC.ASCC_ID))
                        .where(ASCC_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetch(record -> new AsccManifest(
                                record.get(ASCC_MANIFEST.ASCC_MANIFEST_ID),
                                record.get(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID),
                                record.get(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID),
                                record.get(ASCC.SEQ_KEY),
                                record.get(ASCC.CARDINALITY_MIN),
                                record.get(ASCC.CARDINALITY_MAX)
                        )).stream()
                        .collect(groupingBy(AsccManifest::getFromAccManifestId));
        bccManifestMap =
                dslContext.select(
                        BCC_MANIFEST.BCC_MANIFEST_ID,
                        BCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                        BCC_MANIFEST.TO_BCCP_MANIFEST_ID,
                        BCC.SEQ_KEY,
                        BCC.CARDINALITY_MIN,
                        BCC.CARDINALITY_MAX)
                        .from(BCC_MANIFEST)
                        .join(BCC).on(BCC_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                        .where(BCC_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetch(record -> new BccManifest(
                                record.get(BCC_MANIFEST.BCC_MANIFEST_ID),
                                record.get(BCC_MANIFEST.FROM_ACC_MANIFEST_ID),
                                record.get(BCC_MANIFEST.TO_BCCP_MANIFEST_ID),
                                record.get(BCC.SEQ_KEY),
                                record.get(BCC.CARDINALITY_MIN),
                                record.get(BCC.CARDINALITY_MAX)
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
                    AccManifest basedAccManifest = accManifestMap.get(node.getBasedManifestId());
                    Node basedAccNode = Node.toNode(Node.NodeType.ACC, basedAccManifest.getAccManifestId());
                    basedAccNode.setBasedManifestId(basedAccNode.getBasedManifestId());
                    basedAccNode.put("objectClassTerm", basedAccManifest.getObjectClassTerm());
                    children.add(basedAccNode);
                }

                List<Assoc> assocs = new ArrayList();
                assocs.addAll(asccManifestMap.getOrDefault(node.getManifestId(), Collections.emptyList()));
                assocs.addAll(bccManifestMap.getOrDefault(node.getManifestId(), Collections.emptyList()));
                Collections.sort(assocs, Comparator.comparing(Assoc::getSeqKey));
                children.addAll(
                        assocs.stream().map(e -> {
                            if (e instanceof AsccManifest) {
                                AsccManifest asccManifest = (AsccManifest) e;
                                Node asccNode = Node.toNode(Node.NodeType.ASCC, asccManifest.getAsccManifestId());
                                asccNode.setLinkedManifestId(asccManifest.getToAsccpManifestId());
                                asccNode.put("cardinalityMin", asccManifest.getCardinalityMin());
                                asccNode.put("cardinalityMax", asccManifest.getCardinalityMax());
                                return asccNode;
                            } else {
                                BccManifest bccManifest = (BccManifest) e;
                                Node bccNode = Node.toNode(Node.NodeType.BCC, bccManifest.getBccManifestId());
                                bccNode.setLinkedManifestId(bccManifest.getToBccpManifestId());
                                bccNode.put("cardinalityMin", bccManifest.getCardinalityMin());
                                bccNode.put("cardinalityMax", bccManifest.getCardinalityMax());
                                return bccNode;
                            }
                        }).collect(Collectors.toList())
                );

                return children;

            case ASCC:
                AsccpManifest asccpManifest = asccpManifestMap.get(node.getLinkedManifestId());
                if (asccpManifest == null) {
                    return Collections.emptyList();
                }
                Node asccpNode = Node.toNode(Node.NodeType.ASCCP, asccpManifest.getAsccpManifestId());
                asccpNode.setLinkedManifestId(asccpManifest.getRoleOfAccManifestId());
                asccpNode.put("propertyTerm", asccpManifest.getPropertyTerm());
                return Arrays.asList(asccpNode);

            case BCC:
                BccpManifest bccpManifest = bccpManifestMap.get(node.getLinkedManifestId());
                if (bccpManifest == null) {
                    return Collections.emptyList();
                }
                Node bccpNode = Node.toNode(Node.NodeType.BCCP, bccpManifest.getBccpManifestId());
                bccpNode.setLinkedManifestId(bccpManifest.getBdtManifestId());
                bccpNode.put("propertyTerm", bccpManifest.getPropertyTerm());
                bccpNode.put("representationTerm", bccpManifest.getRepresentationTerm());
                return Arrays.asList(bccpNode);

            case ASCCP:
                AccManifest accManifest = accManifestMap.get(node.getLinkedManifestId());
                if (accManifest == null) {
                    return Collections.emptyList();
                }
                Node accNode = Node.toNode(Node.NodeType.ACC, accManifest.getAccManifestId());
                accNode.setBasedManifestId(accManifest.getBasedAccManifestId());
                accNode.put("objectClassTerm", accManifest.getObjectClassTerm());
                return Arrays.asList(accNode);

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
