package org.oagi.srt.gateway.http.bie_management.bie_edit;

import org.oagi.srt.gateway.http.Trackable;

public class BieEditAcc extends Trackable {
    private long accId;
    private String guid;
    private Long basedAccId;
    private long currentAccId;

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

    public long getBasedAccId() {
        return (basedAccId != null) ? basedAccId : 0L;
    }

    public void setBasedAccId(Long basedAccId) {
        this.basedAccId = basedAccId;
    }

    public long getCurrentAccId() {
        return currentAccId;
    }

    public void setCurrentAccId(long currentAccId) {
        this.currentAccId = currentAccId;
    }
}
