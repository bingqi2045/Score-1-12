package org.oagi.score.repo.api.bie.model;

import org.oagi.score.repo.api.base.Response;

import java.math.BigInteger;
import java.util.List;

public class GetAssignedBusinessContextResponse extends Response {

    private final List<String> businessContextIdList;

    public GetAssignedBusinessContextResponse(List<String> businessContextIdList) {
        this.businessContextIdList = businessContextIdList;
    }

    public List<String> getBusinessContextIdList() {
        return businessContextIdList;
    }
}
