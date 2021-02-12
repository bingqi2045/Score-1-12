package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Request;

import java.math.BigInteger;

public class DeleteModuleSetReleaseRequest extends Request {

    public BigInteger getModuleSetReleaseId() {
        return moduleSetReleaseId;
    }

    public void setModuleSetReleaseId(BigInteger moduleSetReleaseId) {
        this.moduleSetReleaseId = moduleSetReleaseId;
    }

    private BigInteger moduleSetReleaseId;
}
