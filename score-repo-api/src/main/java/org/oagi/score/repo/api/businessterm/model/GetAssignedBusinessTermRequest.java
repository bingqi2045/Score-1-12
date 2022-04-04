package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class GetAssignedBusinessTermRequest extends Request {

    private BigInteger assignedBtId;
    private String bieType;

    public GetAssignedBusinessTermRequest(ScoreUser requester) {
        super(requester);
    }

    public BigInteger getAssignedBtId() {
        return assignedBtId;
    }

    public void setAssignedBtId(BigInteger assignedBtId) {
        this.assignedBtId = assignedBtId;
    }

    public String getBieType() {
        return bieType;
    }

    public void setBieType(String bieType) {
        this.bieType = bieType;
    }

    public GetAssignedBusinessTermRequest withAssignedBtId(BigInteger assignedBtId) {
        this.setAssignedBtId(assignedBtId);
        return this;
    }

    public GetAssignedBusinessTermRequest withBieType(String bieType) {
        this.setBieType(bieType);
        return this;
    }
}
