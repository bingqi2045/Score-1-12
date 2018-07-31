package org.oagi.srt.gateway.http.bie_management.bie_edit;

public class BieEditBbie {

    private long bbieId;
    private long fromAbieId;
    private long toBbiepId;
    private long basedBccId;
    private boolean used;

    public long getBbieId() {
        return bbieId;
    }

    public void setBbieId(long bbieId) {
        this.bbieId = bbieId;
    }

    public long getFromAbieId() {
        return fromAbieId;
    }

    public void setFromAbieId(long fromAbieId) {
        this.fromAbieId = fromAbieId;
    }

    public long getToBbiepId() {
        return toBbiepId;
    }

    public void setToBbiepId(long toBbiepId) {
        this.toBbiepId = toBbiepId;
    }

    public long getBasedBccId() {
        return basedBccId;
    }

    public void setBasedBccId(long basedBccId) {
        this.basedBccId = basedBccId;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
