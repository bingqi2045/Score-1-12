package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class GetAssignedBusinessTermRequest extends Request {

    private String assignedBizTermId;
    private String bieType;

    public GetAssignedBusinessTermRequest(ScoreUser requester) {
        super(requester);
    }

    public String getAssignedBizTermId() {
        return assignedBizTermId;
    }

    public void setAssignedBizTermId(String assignedBizTermId) {
        this.assignedBizTermId = assignedBizTermId;
    }

    public String getBieType() {
        return bieType;
    }

    public void setBieType(String bieType) {
        this.bieType = bieType;
    }

    public GetAssignedBusinessTermRequest withAssignedBizTermId(String assignedBizTermId) {
        this.setAssignedBizTermId(assignedBizTermId);
        return this;
    }

    public GetAssignedBusinessTermRequest withBieType(String bieType) {
        this.setBieType(bieType);
        return this;
    }
}
