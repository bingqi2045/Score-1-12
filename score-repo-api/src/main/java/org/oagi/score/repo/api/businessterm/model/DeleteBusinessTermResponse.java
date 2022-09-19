package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Response;

import java.util.List;

public class DeleteBusinessTermResponse extends Response {

    private final List<String> businessTermIdList;

    public DeleteBusinessTermResponse(List<String> businessTermIdList) {
        this.businessTermIdList = businessTermIdList;
    }

    public List<String> getBusinessTermIdList() {
        return businessTermIdList;
    }

    public boolean contains(String businessTermId) {
        return this.businessTermIdList.contains(businessTermId);
    }

    public boolean containsAll(List<String> businessTermIdList) {
        for (String businessTermId : businessTermIdList) {
            if (!this.businessTermIdList.contains(businessTermId)) {
                return false;
            }
        }
        return true;
    }
}
