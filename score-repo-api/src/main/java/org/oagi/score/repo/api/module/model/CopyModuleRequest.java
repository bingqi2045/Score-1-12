package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class CopyModuleRequest extends Request {

    private BigInteger moduleSetId;

    private BigInteger targetModuleId;

    public BigInteger getModuleSetId() {
        return moduleSetId;
    }

    public void setModuleSetId(BigInteger moduleSetId) {
        this.moduleSetId = moduleSetId;
    }

    public BigInteger getTargetModuleId() {
        return targetModuleId;
    }

    public void setTargetModuleId(BigInteger targetModuleId) {
        this.targetModuleId = targetModuleId;
    }

    public BigInteger getParentModuleId() {
        return parentModuleId;
    }

    public void setParentModuleId(BigInteger parentModuleId) {
        this.parentModuleId = parentModuleId;
    }

    private BigInteger parentModuleId;

    public CopyModuleRequest(ScoreUser requester) {
        super(requester);
    }
}
