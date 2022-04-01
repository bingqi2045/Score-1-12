package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Response;

import java.math.BigInteger;
import java.util.List;

public class DeleteAssignedBusinessTermResponse extends Response {

    private final List<BieToAssign> assignedBtList;

    public DeleteAssignedBusinessTermResponse(List<BieToAssign> assignedBtIdList) {
        this.assignedBtList = assignedBtIdList;
    }

    public List<BieToAssign> getAssignedBtList() {
        return assignedBtList;
    }

    public boolean contains(BieToAssign assignedBtId) {
        return this.assignedBtList.contains(assignedBtId);
    }

    public boolean containsAll(List<BieToAssign> assignedBtIdList) {
        for (BieToAssign assignedBtId : assignedBtIdList) {
            if (!this.assignedBtList.contains(assignedBtId)) {
                return false;
            }
        }
        return true;
    }
}
