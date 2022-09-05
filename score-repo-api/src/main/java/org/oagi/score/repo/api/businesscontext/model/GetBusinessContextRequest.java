package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

public class GetBusinessContextRequest extends Request {

    private String businessContextId;

    public GetBusinessContextRequest(ScoreUser requester) {
        super(requester);
    }

    public String getBusinessContextId() {
        return businessContextId;
    }

    public void setBusinessContextId(String businessContextId) {
        this.businessContextId = businessContextId;
    }

    public GetBusinessContextRequest withBusinessContextId(String businessContextId) {
        this.setBusinessContextId(businessContextId);
        return this;
    }
}
