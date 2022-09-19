package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Response;

import java.util.List;

public class AssignBusinessTermResponse extends Response {

    private final List<String> assignedBusinessTermIdList;

    public AssignBusinessTermResponse(List<String> assignedBusinessTermIdList) {
        this.assignedBusinessTermIdList = assignedBusinessTermIdList;
    }

    public List<String> getAssignedBusinessTermIdList() {
        return assignedBusinessTermIdList;
    }
}
