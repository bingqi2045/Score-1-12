package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Response;

import java.math.BigInteger;
import java.util.List;

public class AssignBusinessTermResponse extends Response {

    private final List<BigInteger> assignedBusinessTermId;

    public AssignBusinessTermResponse(List<BigInteger> assignedBusinessTermId) {
        this.assignedBusinessTermId = assignedBusinessTermId;
    }

    public List<BigInteger> getAssignedBusinessTermId() {
        return assignedBusinessTermId;
    }
}
