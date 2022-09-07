package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class DeleteModuleSetAssignmentRequest extends Request {

    public DeleteModuleSetAssignmentRequest(ScoreUser requester) {
        super(requester);
    }

    public String getModuleSetId() {
        return moduleSetId;
    }

    public void setModuleSetId(String moduleSetId) {
        this.moduleSetId = moduleSetId;
    }

    private String moduleSetId;
    private String moduleId;
    private String moduleDirId;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleDirId() {
        return moduleDirId;
    }

    public void setModuleDirId(String moduleDirId) {
        this.moduleDirId = moduleDirId;
    }
}
