package org.oagi.srt.gateway.http.api.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.jooq.DSLContext;
import org.jooq.tools.StringUtils;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.*;

import javax.xml.bind.annotation.XmlTransient;
import java.util.*;

import static org.oagi.srt.entity.jooq.Tables.*;

@Data
public class Graph {

    @XmlTransient
    @JsonIgnore
    private transient DSLContext dslContext;

    @XmlTransient
    @JsonIgnore
    private transient Map<String, List<ULong>> nodeManifestIds = new HashMap();

    private Map<String, Map<String, Object>> nodes = new LinkedHashMap();
    private Map<String, Edge> edges = new LinkedHashMap();

    public Graph(DSLContext dslContext) {
        this.dslContext = dslContext;

        Arrays.asList("acc", "asccp", "bccp", "bdt", "bdtsc").stream().forEach(e -> {
            nodeManifestIds.put(e, new ArrayList());
        });
    }

    public boolean addNode(Object node) {
        String key = getKey(node);
        if (nodes.containsKey(key)) {
            return false;
        }

        Map<String, Object> metadata = new LinkedHashMap();
        nodes.put(key, metadata);

        String type;
        ULong manifestId;

        if (node instanceof AccManifestRecord) {
            AccManifestRecord accManifest = (AccManifestRecord) node;
            type = "acc";
            manifestId = accManifest.getAccManifestId();
        } else if (node instanceof AsccpManifestRecord) {
            AsccpManifestRecord asccpManifest = (AsccpManifestRecord) node;
            type = "asccp";
            manifestId = asccpManifest.getAsccpManifestId();
        } else if (node instanceof BccpManifestRecord) {
            BccpManifestRecord bccpManifest = (BccpManifestRecord) node;
            type = "bccp";
            manifestId = bccpManifest.getBccpManifestId();
        } else if (node instanceof DtManifestRecord) {
            DtManifestRecord dtManifest = (DtManifestRecord) node;
            type = "bdt";
            manifestId = dtManifest.getDtManifestId();
        } else if (node instanceof DtScManifestRecord) {
            DtScManifestRecord dtScManifest = (DtScManifestRecord) node;
            type = "bdtsc";
            manifestId = dtScManifest.getDtScManifestId();
        } else {
            throw new IllegalArgumentException();
        }

        metadata.put("type", type);
        metadata.put("manifestId", manifestId);

        nodeManifestIds.get(type).add(manifestId);
        return true;
    }

    public void addEdges(Object source, List children) {
        String sourceKey = getKey(source);
        if (edges.containsKey(sourceKey)) {
            return;
        }
        Edge edge = new Edge();
        children.stream().forEach(e -> {
            edge.addTarget(getKey(e));
        });
        edges.put(sourceKey, edge);
    }

    private String getKey(Object node) {
        if (node instanceof AccManifestRecord) {
            AccManifestRecord accManifest = (AccManifestRecord) node;
            return "acc" + accManifest.getAccManifestId();
        } else if (node instanceof AsccpManifestRecord) {
            AsccpManifestRecord asccpManifest = (AsccpManifestRecord) node;
            return "asccp" + asccpManifest.getAsccpManifestId();
        } else if (node instanceof BccpManifestRecord) {
            BccpManifestRecord bccpManifest = (BccpManifestRecord) node;
            return "bccp" + bccpManifest.getBccpManifestId();
        } else if (node instanceof DtManifestRecord) {
            DtManifestRecord dtManifest = (DtManifestRecord) node;
            return "bdt" + dtManifest.getDtManifestId();
        } else if (node instanceof DtScManifestRecord) {
            DtScManifestRecord dtScManifest = (DtScManifestRecord) node;
            return "bdtsc" + dtScManifest.getDtScManifestId();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void build() {
        nodeManifestIds.entrySet().stream().forEach(entry -> {
            switch (entry.getKey()) {
                case "acc":
                    dslContext.select(ACC_MANIFEST.ACC_MANIFEST_ID, ACC.OBJECT_CLASS_TERM)
                            .from(ACC)
                            .join(ACC_MANIFEST).on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                            .where(ACC_MANIFEST.ACC_MANIFEST_ID.in(entry.getValue()))
                            .fetchStream().forEach(record -> {
                        Map<String, Object> metadata = nodes.get("acc" + record.value1());
                        metadata.put("objectClassTerm", record.value2());
                    });
                    break;

                case "asccp":
                    dslContext.select(ASCCP_MANIFEST.ASCCP_MANIFEST_ID, ASCCP.PROPERTY_TERM)
                            .from(ASCCP)
                            .join(ASCCP_MANIFEST).on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                            .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.in(entry.getValue()))
                            .fetchStream().forEach(record -> {
                        Map<String, Object> metadata = nodes.get("asccp" + record.value1());
                        metadata.put("propertyTerm", record.value2());
                    });
                    break;

                case "bccp":
                    dslContext.select(BCCP_MANIFEST.BCCP_MANIFEST_ID, BCCP.PROPERTY_TERM, BCCP.REPRESENTATION_TERM)
                            .from(BCCP)
                            .join(BCCP_MANIFEST).on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                            .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.in(entry.getValue()))
                            .fetchStream().forEach(record -> {
                        Map<String, Object> metadata = nodes.get("bccp" + record.value1());
                        metadata.put("propertyTerm", record.value2());
                        metadata.put("representationTerm", record.value3());
                    });
                    break;

                case "bdt":
                    dslContext.select(DT_MANIFEST.DT_MANIFEST_ID, DT.DATA_TYPE_TERM, DT.QUALIFIER)
                            .from(DT)
                            .join(DT_MANIFEST).on(DT.DT_ID.eq(DT_MANIFEST.DT_ID))
                            .where(DT_MANIFEST.DT_MANIFEST_ID.in(entry.getValue()))
                            .fetchStream().forEach(record -> {
                        Map<String, Object> metadata = nodes.get("bdt" + record.value1());
                        metadata.put("dataTypeTerm", record.value2());
                        String qualifier = record.value3();
                        if (!StringUtils.isEmpty(qualifier)) {
                            metadata.put("qualifier", qualifier);
                        }
                    });
                    break;

                case "bdtsc":
                    dslContext.select(DT_SC_MANIFEST.DT_SC_MANIFEST_ID, DT_SC.PROPERTY_TERM, DT_SC.REPRESENTATION_TERM)
                            .from(DT_SC)
                            .join(DT_SC_MANIFEST).on(DT_SC.DT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                            .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.in(entry.getValue()))
                            .fetchStream().forEach(record -> {
                        Map<String, Object> metadata = nodes.get("bdtsc" + record.value1());
                        metadata.put("propertyTerm", record.value2());
                        metadata.put("representationTerm", record.value3());
                    });
                    break;
            }
        });
    }

}
