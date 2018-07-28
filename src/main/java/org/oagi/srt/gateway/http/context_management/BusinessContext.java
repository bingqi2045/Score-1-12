package org.oagi.srt.gateway.http.context_management;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BusinessContext {
    private long bizCtxId;
    private String guid;
    private String name;
    private Date lastUpdateTimestamp;
    private List<BusinessContextValue> bizCtxValues;

    public long getBizCtxId() {
        return bizCtxId;
    }

    public void setBizCtxId(long bizCtxId) {
        this.bizCtxId = bizCtxId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(Date lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public List<BusinessContextValue> getBizCtxValues() {
        return (bizCtxValues != null) ? bizCtxValues : Collections.emptyList();
    }

    public void setBizCtxValues(List<BusinessContextValue> bizCtxValues) {
        this.bizCtxValues = bizCtxValues;
    }
}
