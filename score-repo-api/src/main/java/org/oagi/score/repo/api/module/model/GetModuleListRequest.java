package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.PaginationRequest;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class GetModuleListRequest extends PaginationRequest<Module> {

    public GetModuleListRequest(ScoreUser requester) {
        super(requester, Module.class);
    }

    private BigInteger moduleSetId;

    public BigInteger getModuleSetId() {
        return moduleSetId;
    }

    public void setModuleSetId(BigInteger moduleSetId) {
        this.moduleSetId = moduleSetId;
    }
}
