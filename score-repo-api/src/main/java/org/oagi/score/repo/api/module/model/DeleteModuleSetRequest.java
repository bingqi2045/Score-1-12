package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Request;

import java.math.BigInteger;

public class DeleteModuleSetRequest extends Request {

    public BigInteger getModuleSetId() {
        return moduleSetId;
    }

    public void setModuleSetId(BigInteger moduleSetId) {
        this.moduleSetId = moduleSetId;
    }

    private BigInteger moduleSetId;
}
