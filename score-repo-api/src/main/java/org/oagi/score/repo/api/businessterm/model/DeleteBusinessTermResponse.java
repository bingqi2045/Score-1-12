package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Response;

import java.math.BigInteger;
import java.util.List;

public class DeleteBusinessTermResponse extends Response {

    private final List<BigInteger> businessTermIdList;

    public DeleteBusinessTermResponse(List<BigInteger> businessTermIdList) {
        this.businessTermIdList = businessTermIdList;
    }

    public List<BigInteger> getBusinessTermIdList() {
        return businessTermIdList;
    }

    public boolean contains(BigInteger contextSchemeId) {
        return this.businessTermIdList.contains(contextSchemeId);
    }

    public boolean containsAll(List<BigInteger> businessTermIdList) {
        for (BigInteger businessTermID : businessTermIdList) {
            if (!this.businessTermIdList.contains(businessTermID)) {
                return false;
            }
        }
        return true;
    }
}
