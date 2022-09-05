package org.oagi.score.repo.api.bie.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

public class GetAssignedBusinessContextRequest extends Request {

    private String topLevelAsbiepId;

    public GetAssignedBusinessContextRequest(ScoreUser requester) {
        super(requester);
    }

    public String getTopLevelAsbiepId() {
        return topLevelAsbiepId;
    }

    public void setTopLevelAsbiepId(String topLevelAsbiepId) {
        this.topLevelAsbiepId = topLevelAsbiepId;
    }

    public GetAssignedBusinessContextRequest withTopLevelAsbiepId(String topLevelAsbiepId) {
        setTopLevelAsbiepId(topLevelAsbiepId);
        return this;
    }

}
