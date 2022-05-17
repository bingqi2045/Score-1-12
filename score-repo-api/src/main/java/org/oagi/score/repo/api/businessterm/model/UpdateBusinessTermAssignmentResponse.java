package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Response;

import java.math.BigInteger;

public class UpdateBusinessTermAssignmentResponse extends Response {

    private final BigInteger assignedBtId;
    private final String bieType;
    private final boolean changed;

    public UpdateBusinessTermAssignmentResponse(BigInteger assignedBtId, String bieType, boolean changed) {
        this.assignedBtId = assignedBtId;
        this.bieType = bieType;
        this.changed = changed;
    }

    public BigInteger getAssignedBtId() {
        return assignedBtId;
    }

    public String getBieType() {
        return bieType;
    }

    public boolean isChanged() {
        return changed;
    }
}
