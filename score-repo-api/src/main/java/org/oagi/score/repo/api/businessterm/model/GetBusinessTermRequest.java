package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

public class GetBusinessTermRequest extends Request {

    private String businessTermId;

    public GetBusinessTermRequest(ScoreUser requester) {
        super(requester);
    }

    public String getBusinessTermId() {
        return businessTermId;
    }

    public void setBusinessTermId(String businessTermId) {
        this.businessTermId = businessTermId;
    }

    public GetBusinessTermRequest withBusinessTermId(String businessTermId) {
        this.setBusinessTermId(businessTermId);
        return this;
    }
}
