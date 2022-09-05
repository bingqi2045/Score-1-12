package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CreateBusinessContextResponse extends Response {

    private final String businessContextId;

    private Collection<BusinessContextValue> businessContextValueList;

    public CreateBusinessContextResponse(String businessContextId) {
        this.businessContextId = businessContextId;
    }

    public String getBusinessContextId() {
        return businessContextId;
    }

    public Collection<BusinessContextValue> getBusinessContextValues() {
        return (businessContextValueList == null) ? Collections.emptyList() : businessContextValueList;
    }

    public void setBusinessContextValueList(Collection<BusinessContextValue> businessContextValueList) {
        this.businessContextValueList = businessContextValueList;
    }

    public void addBusinessContextValue(BusinessContextValue businessContextValue) {
        if (this.businessContextValueList == null) {
            this.businessContextValueList = new ArrayList();
        }

        this.businessContextValueList.add(businessContextValue);
    }
}
