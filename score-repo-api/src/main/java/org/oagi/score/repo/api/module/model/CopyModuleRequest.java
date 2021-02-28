package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class CopyModuleRequest extends Request {

    private BigInteger moduleSetId;

    private BigInteger moduleId;

    public BigInteger getModuleSetId() {
        return moduleSetId;
    }

    public void setModuleSetId(BigInteger moduleSetId) {
        this.moduleSetId = moduleSetId;
    }

    public BigInteger getModuleId() {
        return moduleId;
    }

    public void setModuleId(BigInteger moduleId) {
        this.moduleId = moduleId;
    }

    public BigInteger getCopyPosDirId() {
        return copyPosDirId;
    }

    public void setCopyPosDirId(BigInteger copyPosDirId) {
        this.copyPosDirId = copyPosDirId;
    }

    private BigInteger copyPosDirId;

}
