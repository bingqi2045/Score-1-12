package org.oagi.srt.gateway.http.bie_management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BieTreeNode {

    private String type;
    private long bieId;
    private long ccId;
    private List<BieTreeNode> children;

    public BieTreeNode(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getBieId() {
        return bieId;
    }

    public void setBieId(long bieId) {
        this.bieId = bieId;
    }

    public long getCcId() {
        return ccId;
    }

    public void setCcId(long ccId) {
        this.ccId = ccId;
    }

    public List<BieTreeNode> getChildren() {
        return (children != null) ? children : Collections.emptyList();
    }

    public void setChildren(List<BieTreeNode> children) {
        this.children = children;
    }

    public void addChildren(BieTreeNode bieTreeNode) {
        if (children == null) {
            children = new ArrayList();
        }
        children.add(bieTreeNode);
    }
}
