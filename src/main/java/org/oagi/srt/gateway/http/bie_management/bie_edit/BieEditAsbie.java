package org.oagi.srt.gateway.http.bie_management.bie_edit;

public class BieEditAsbie {
    private long asbieId;
    private long fromAbieId;
    private long toAsbiepId;
    private long basedAsccId;
    private boolean used;

    public long getAsbieId() {
        return asbieId;
    }

    public void setAsbieId(long asbieId) {
        this.asbieId = asbieId;
    }

    public long getFromAbieId() {
        return fromAbieId;
    }

    public void setFromAbieId(long fromAbieId) {
        this.fromAbieId = fromAbieId;
    }

    public long getToAsbiepId() {
        return toAsbiepId;
    }

    public void setToAsbiepId(long toAsbiepId) {
        this.toAsbiepId = toAsbiepId;
    }

    public long getBasedAsccId() {
        return basedAsccId;
    }

    public void setBasedAsccId(long basedAsccId) {
        this.basedAsccId = basedAsccId;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
