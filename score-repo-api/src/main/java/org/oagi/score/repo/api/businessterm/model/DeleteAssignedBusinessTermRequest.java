package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DeleteAssignedBusinessTermRequest extends Request {

    private List<BieToAssign> assignedBtList = Collections.emptyList();

    public DeleteAssignedBusinessTermRequest(ScoreUser requester) {
        super(requester);
    }

    public List<BieToAssign> getAssignedBtList() {
        return assignedBtList;
    }

    public void setBusinessTermId(BieToAssign assignedBtId) {
        if (assignedBtId != null) {
            this.assignedBtList = Arrays.asList(assignedBtId);
        }
    }

    public DeleteAssignedBusinessTermRequest withAssignedBtId(BieToAssign assignedBtId) {
        this.setBusinessTermId(assignedBtId);
        return this;
    }

    public void setAssignedBtList(List<BieToAssign> assignedBtList) {
        if (assignedBtList != null) {
            this.assignedBtList = assignedBtList;
        }
    }

    public DeleteAssignedBusinessTermRequest withAssignedBtList(List<BieToAssign> assignedBtId) {
        this.setAssignedBtList(assignedBtId);
        return this;
    }

}
