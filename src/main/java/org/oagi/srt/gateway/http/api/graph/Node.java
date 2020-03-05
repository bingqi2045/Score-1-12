package org.oagi.srt.gateway.http.api.graph;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.*;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(using = NodeSerializer.class)
public class Node {

    public enum NodeType {
        ACC,
        ASCCP,
        BCCP,
        BDT,
        BDTSC;

        public String toString() {
            return this.name().toLowerCase();
        }
    }

    private NodeType type;
    private ULong manifestId;
    private ULong basedManifestId;
    private ULong linkedManifestId;

    private Map<String, String> properties = new HashMap();

    public Node(NodeType type, ULong manifestId) {
        setType(type);
        setManifestId(manifestId);
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public ULong getManifestId() {
        return manifestId;
    }

    public void setManifestId(ULong manifestId) {
        this.manifestId = manifestId;
    }

    public ULong getBasedManifestId() {
        return basedManifestId;
    }

    public void setBasedManifestId(ULong basedManifestId) {
        this.basedManifestId = basedManifestId;
    }

    public ULong getLinkedManifestId() {
        return linkedManifestId;
    }

    public void setLinkedManifestId(ULong linkedManifestId) {
        this.linkedManifestId = linkedManifestId;
    }

    public String getTypeAsString() {
        return getType().toString();
    }

    public String getKey() {
        return getTypeAsString() + getManifestId();
    }

    public void put(String key, String value) {
        this.properties.put(key, value);
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public static Node toNode(AccManifestRecord accManifest) {
        Node node = new Node(NodeType.ACC, accManifest.getAccManifestId());
        node.setBasedManifestId(accManifest.getBasedAccManifestId());
        return node;
    }

    public static Node toNode(AsccpManifestRecord asccpManifest) {
        Node node = new Node(NodeType.ASCCP, asccpManifest.getAsccpManifestId());
        node.setLinkedManifestId(asccpManifest.getRoleOfAccManifestId());
        return node;
    }

    public static Node toNode(BccpManifestRecord bccpManifest) {
        Node node = new Node(NodeType.BCCP, bccpManifest.getBccpManifestId());
        node.setLinkedManifestId(bccpManifest.getBdtManifestId());
        return node;
    }

    public static Node toNode(DtManifestRecord dtManifest) {
        return new Node(NodeType.BDT, dtManifest.getDtManifestId());
    }

    public static Node toNode(DtScManifestRecord dtScManifest) {
        return new Node(NodeType.BDTSC, dtScManifest.getDtScManifestId());
    }

    public boolean hasTerm(String query) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String value = entry.getValue();
            if (value.toLowerCase().contains(query)) {
                return true;
            }
        }
        return false;
    }
}
