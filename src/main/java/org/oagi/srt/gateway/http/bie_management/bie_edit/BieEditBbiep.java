package org.oagi.srt.gateway.http.bie_management.bie_edit;

public class BieEditBbiep {
    private long bbiepId;
    private long basedBccpId;
    private boolean used;

    public long getBbiepId() {
        return bbiepId;
    }

    public void setBbiepId(long bbiepId) {
        this.bbiepId = bbiepId;
    }

    public long getBasedBccpId() {
        return basedBccpId;
    }

    public void setBasedBccpId(long basedBccpId) {
        this.basedBccpId = basedBccpId;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
