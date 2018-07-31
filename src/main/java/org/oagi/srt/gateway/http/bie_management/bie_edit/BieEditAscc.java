package org.oagi.srt.gateway.http.bie_management.bie_edit;

import org.oagi.srt.gateway.http.Trackable;

public class BieEditAscc extends Trackable implements SeqKeySupportable {
    private long asccId;
    private String guid;
    private long fromAccId;
    private long toAsccpId;
    private int seqKey;
    private long currentAsccId;

    public long getAsccId() {
        return asccId;
    }

    public void setAsccId(long asccId) {
        this.asccId = asccId;
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

    public long getToAsccpId() {
        return toAsccpId;
    }

    public void setToAsccpId(long toAsccpId) {
        this.toAsccpId = toAsccpId;
    }

    public int getSeqKey() {
        return seqKey;
    }

    public void setSeqKey(int seqKey) {
        this.seqKey = seqKey;
    }

    public long getCurrentAsccId() {
        return currentAsccId;
    }

    public void setCurrentAsccId(long currentAsccId) {
        this.currentAsccId = currentAsccId;
    }
}
