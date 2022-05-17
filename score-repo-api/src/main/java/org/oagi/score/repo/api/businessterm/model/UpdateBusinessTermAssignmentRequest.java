package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class UpdateBusinessTermAssignmentRequest extends Request {

    private BigInteger assignedBtId;

    private BigInteger bieId;

    private String bieType;

    private String typeCode;

    private String primaryIndicator;

    public UpdateBusinessTermAssignmentRequest(ScoreUser requester) {
        super(requester);
    }

    public BigInteger getAssignedBtId() {
        return assignedBtId;
    }

    public void setAssignedBtId(BigInteger assignedBtId) {
        this.assignedBtId = assignedBtId;
    }

    public UpdateBusinessTermAssignmentRequest withAssignedBtId(BigInteger assignedBtId) {
        this.setAssignedBtId(assignedBtId);
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

    public BigInteger getBieId() {
        return bieId;
    }

    public void setBieId(BigInteger bieId) {
        this.bieId = bieId;
    }

    @Override
    public String toString() {
        return "UpdateBusinessTermAssignmentRequest{" +
                "assignedBtId=" + assignedBtId +
                ", bieId=" + bieId +
                ", bieType='" + bieType + '\'' +
                ", typeCode='" + typeCode + '\'' +
                ", primaryIndicator='" + primaryIndicator + '\'' +
                '}';
    }
}
