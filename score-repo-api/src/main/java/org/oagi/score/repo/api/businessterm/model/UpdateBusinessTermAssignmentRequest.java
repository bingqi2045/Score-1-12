package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class UpdateBusinessTermAssignmentRequest extends Request {

    private String assignedBizTermId;

    private String bieId;

    private String bieType;

    private String typeCode;

    private String primaryIndicator;

    public UpdateBusinessTermAssignmentRequest(ScoreUser requester) {
        super(requester);
    }

    public String getAssignedBizTermId() {
        return assignedBizTermId;
    }

    public void setAssignedBizTermId(String assignedBizTermId) {
        this.assignedBizTermId = assignedBizTermId;
    }

    public UpdateBusinessTermAssignmentRequest withAssignedBizTermId(String assignedBizTermId) {
        this.setAssignedBizTermId(assignedBizTermId);
        return this;
    }

    public String getBieType() {
        return bieType;
    }

    public void setBieType(String bieType) {
        this.bieType = bieType;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getPrimaryIndicator() {
        return primaryIndicator;
    }

    public void setPrimaryIndicator(String primaryIndicator) {
        this.primaryIndicator = primaryIndicator;
    }

    public String getBieId() {
        return bieId;
    }

    public void setBieId(String bieId) {
        this.bieId = bieId;
    }

    @Override
    public String toString() {
        return "UpdateBusinessTermAssignmentRequest{" +
                "assignedBizTermId=" + assignedBizTermId +
                ", bieId=" + bieId +
                ", bieType='" + bieType + '\'' +
                ", typeCode='" + typeCode + '\'' +
                ", primaryIndicator='" + primaryIndicator + '\'' +
                '}';
    }
}
