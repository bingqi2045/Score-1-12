package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Response;

public class AssignBusinessTermResponse extends Response {

    private final AssignedBusinessTerm assignedBusinessTerm;

    public AssignBusinessTermResponse(AssignedBusinessTerm assignedBusinessTerm) {
        this.assignedBusinessTerm = assignedBusinessTerm;
    }

    public final AssignedBusinessTerm getAssignedBusinessTerm() {
        return assignedBusinessTerm;
    }

}
