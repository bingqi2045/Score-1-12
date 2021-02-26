package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Request;

import java.math.BigInteger;

public class DeleteModuleDirRequest extends Request {

    public BigInteger getModuleDirId() {
        return moduleDirId;
    }

    public void setModuleDirId(BigInteger moduleDirId) {
        this.moduleDirId = moduleDirId;
    }

    private BigInteger moduleDirId;
}
