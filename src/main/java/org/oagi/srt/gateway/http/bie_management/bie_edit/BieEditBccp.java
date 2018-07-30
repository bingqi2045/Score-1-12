package org.oagi.srt.gateway.http.bie_management.bie_edit;

import org.oagi.srt.gateway.http.Trackable;

public class BieEditBccp extends Trackable {
    private long bccpId;
    private String guid;
    private String propertyTerm;
    private long bdtId;
    private long currentBccpId;

    public long getBccpId() {
        return bccpId;
    }

    public void setBccpId(long bccpId) {
        this.bccpId = bccpId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getPropertyTerm() {
        return propertyTerm;
    }

    public void setPropertyTerm(String propertyTerm) {
        this.propertyTerm = propertyTerm;
    }

    public long getBdtId() {
        return bdtId;
    }

    public void setBdtId(long bdtId) {
        this.bdtId = bdtId;
    }

    public long getCurrentBccpId() {
        return currentBccpId;
    }

    public void setCurrentBccpId(long currentBccpId) {
        this.currentBccpId = currentBccpId;
    }
}
