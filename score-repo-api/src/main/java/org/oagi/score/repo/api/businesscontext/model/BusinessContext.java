package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Auditable;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public class BusinessContext extends Auditable implements Serializable {

    private String businessContextId;

    private String guid;

    private String name;

    private List<BusinessContextValue> businessContextValueList = Collections.emptyList();

    private boolean used;

    public String getBusinessContextId() {
        return businessContextId;
    }

    public void setBusinessContextId(String businessContextId) {
        this.businessContextId = businessContextId;
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

    public List<BusinessContextValue> getBusinessContextValueList() {
        return businessContextValueList;
    }

    public void setBusinessContextValueList(List<BusinessContextValue> businessContextValueList) {
        this.businessContextValueList = businessContextValueList;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
