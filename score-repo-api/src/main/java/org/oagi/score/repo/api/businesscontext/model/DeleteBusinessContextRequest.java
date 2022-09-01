package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DeleteBusinessContextRequest extends Request {

    private List<String> businessContextIdList = Collections.emptyList();

    public DeleteBusinessContextRequest(ScoreUser requester) {
        super(requester);
    }

    public List<String> getBusinessContextIdList() {
        return businessContextIdList;
    }

    public void setBusinessContextId(String businessContextId) {
        if (businessContextId != null) {
            this.businessContextIdList = Arrays.asList(businessContextId);
        }
    }

    public DeleteBusinessContextRequest withBusinessContextId(String businessContextId) {
        this.setBusinessContextId(businessContextId);
        return this;
    }

    public void setBusinessContextIdList(List<String> businessContextIdList) {
        if (businessContextIdList != null) {
            this.businessContextIdList = businessContextIdList;
        }
    }

    public DeleteBusinessContextRequest withBusinessContextIdList(List<String> businessContextIdList) {
        this.setBusinessContextIdList(businessContextIdList);
        return this;
    }

}
