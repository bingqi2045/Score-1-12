package org.oagi.srt.gateway.http.bie_management.bie_edit;

import org.oagi.srt.gateway.http.Trackable;

public class BieEditBcc extends Trackable {
    private long bccId;
    private String guid;
    private long fromAccId;
    private long toBccpId;
    private int seqKey;
    private int entityType;
    private long currentBccId;

    public long getBccId() {
        return bccId;
    }

    public void setBccId(long bccId) {
        this.bccId = bccId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public long getFromAccId() {
        return fromAccId;
    }

    public void setFromAccId(long fromAccId) {
        this.fromAccId = fromAccId;
    }

    public long getToBccpId() {
        return toBccpId;
    }

    public void setToBccpId(long toBccpId) {
        this.toBccpId = toBccpId;
    }

    public int getSeqKey() {
        return seqKey;
    }

    public void setSeqKey(int seqKey) {
        this.seqKey = seqKey;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public long getCurrentBccId() {
        return currentBccId;
    }

    public void setCurrentBccId(long currentBccId) {
        this.currentBccId = currentBccId;
    }
}
