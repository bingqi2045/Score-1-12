package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DeleteBusinessTermRequest extends Request {

    private List<String> businessTermIdList = Collections.emptyList();

    public DeleteBusinessTermRequest(ScoreUser requester) {
        super(requester);
    }

    public List<String> getBusinessTermIdList() {
        return businessTermIdList;
    }

    public void setBusinessTermId(String businessTermId) {
        if (businessTermId != null) {
            this.businessTermIdList = Arrays.asList(businessTermId);
        }
    }

    public DeleteBusinessTermRequest withBusinessTermId(String businessTermId) {
        this.setBusinessTermId(businessTermId);
        return this;
    }

    public void setBusinessTermIdList(List<String> businessTermIdList) {
        if (businessTermIdList != null) {
            this.businessTermIdList = businessTermIdList;
        }
    }

    public DeleteBusinessTermRequest withBusinessTermIdList(List<String> businessTermIdList) {
        this.setBusinessTermIdList(businessTermIdList);
        return this;
    }

}
