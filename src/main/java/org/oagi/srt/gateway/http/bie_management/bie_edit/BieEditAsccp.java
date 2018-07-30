package org.oagi.srt.gateway.http.bie_management.bie_edit;

import org.oagi.srt.gateway.http.Trackable;

public class BieEditAsccp extends Trackable {
    private long asccpId;
    private String guid;
    private String propertyTerm;
    private long roleOfAccId;
    private long currentAsccpId;

    public long getAsccpId() {
        return asccpId;
    }

    public void setAsccpId(long asccpId) {
        this.asccpId = asccpId;
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

    public long getRoleOfAccId() {
        return roleOfAccId;
    }

    public void setRoleOfAccId(long roleOfAccId) {
        this.roleOfAccId = roleOfAccId;
    }

    public long getCurrentAsccpId() {
        return currentAsccpId;
    }

    public void setCurrentAsccpId(long currentAsccpId) {
        this.currentAsccpId = currentAsccpId;
    }
}
