package org.oagi.srt.gateway.http.bie_management.bie_edit;

public class BieEditBbiepNode extends BieEditNode {

    private long bbieId;
    private long bccId;
    private long bbiepId;
    private long bccpId;
    private boolean attribute;

    public long getBbieId() {
        return bbieId;
    }

    public void setBbieId(long bbieId) {
        this.bbieId = bbieId;
    }

    public long getBccId() {
        return bccId;
    }

    public void setBccId(long bccId) {
        this.bccId = bccId;
    }

    public long getBbiepId() {
        return bbiepId;
    }

    public void setBbiepId(long bbiepId) {
        this.bbiepId = bbiepId;
    }

    public long getBccpId() {
        return bccpId;
    }

    public void setBccpId(long bccpId) {
        this.bccpId = bccpId;
    }

    public boolean isAttribute() {
        return attribute;
    }

    public void setAttribute(boolean attribute) {
        this.attribute = attribute;
    }
}
