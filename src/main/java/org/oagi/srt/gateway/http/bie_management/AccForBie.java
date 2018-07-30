package org.oagi.srt.gateway.http.bie_management;

import org.oagi.srt.gateway.http.Trackable;

public class AccForBie extends Trackable {
    private long accId;
    private String guid;

    public long getAccId() {
        return accId;
    }

    public void setAccId(long accId) {
        this.accId = accId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
