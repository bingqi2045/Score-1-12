package org.oagi.score.repo.api.bie.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

public class GetBiePackageRequest extends Request {

    private String topLevelAsbiepId;
    private boolean used;

    public GetBiePackageRequest(ScoreUser requester) {
        super(requester);
    }

    public String getTopLevelAsbiepId() {
        return topLevelAsbiepId;
    }

    public void setTopLevelAsbiepId(String topLevelAsbiepId) {
        this.topLevelAsbiepId = topLevelAsbiepId;
    }

    public GetBiePackageRequest withTopLevelAsbiepId(String topLevelAsbiepId) {
        setTopLevelAsbiepId(topLevelAsbiepId);
        return this;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public GetBiePackageRequest withUsed(boolean used) {
        setUsed(used);
        return this;
    }

}
