package org.oagi.srt.gateway.http.bie_management.bie_edit;

public class BieEditBbieSc {
    private long bbieScId;
    private long bbieId;
    private boolean used;

    public long getBbieScId() {
        return bbieScId;
    }

    public void setBbieScId(long bbieScId) {
        this.bbieScId = bbieScId;
    }

    public long getBbieId() {
        return bbieId;
    }

    public void setBbieId(long bbieId) {
        this.bbieId = bbieId;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
