package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

public class DeleteModuleSetReleaseRequest extends Request {

    public String getModuleSetReleaseId() {
        return moduleSetReleaseId;
    }

    public void setModuleSetReleaseId(String moduleSetReleaseId) {
        this.moduleSetReleaseId = moduleSetReleaseId;
    }

    private String moduleSetReleaseId;

    public DeleteModuleSetReleaseRequest(ScoreUser requester) {
        super(requester);
    }
}
