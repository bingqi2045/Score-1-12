package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

public class GetAssignableCCListRequest extends Request {

    public GetAssignableCCListRequest(ScoreUser requester) {
        super(requester);
    }

    String moduleSetReleaseId;
    String releaseId;

    public String getModuleSetReleaseId() {
        return moduleSetReleaseId;
    }

    public void setModuleSetReleaseId(String moduleSetReleaseId) {
        this.moduleSetReleaseId = moduleSetReleaseId;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }
}
