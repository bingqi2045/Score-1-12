package org.oagi.srt.gateway.http.bie_management;

public class TopLevelAbie {
    private long topLevelAbieId;
    private long abieId;
    private long ownerUserId;
    private long releaseId;
    private int state;

    public long getTopLevelAbieId() {
        return topLevelAbieId;
    }

    public void setTopLevelAbieId(long topLevelAbieId) {
        this.topLevelAbieId = topLevelAbieId;
    }

    public long getAbieId() {
        return abieId;
    }

    public void setAbieId(long abieId) {
        this.abieId = abieId;
    }

    public long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public long getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(long releaseId) {
        this.releaseId = releaseId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
