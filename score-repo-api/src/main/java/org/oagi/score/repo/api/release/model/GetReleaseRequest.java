package org.oagi.score.repo.api.release.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

public class GetReleaseRequest extends Request {

    private String topLevelAsbiepId;

    private String releaseId;

    private String releaseNum;

    public GetReleaseRequest(ScoreUser requester) {
        super(requester);
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public GetReleaseRequest withReleaseId(String releaseId) {
        setReleaseId(releaseId);
        return this;
    }

    public String getTopLevelAsbiepId() {
        return topLevelAsbiepId;
    }

    public void setTopLevelAsbiepId(String topLevelAsbiepId) {
        this.topLevelAsbiepId = topLevelAsbiepId;
    }

    public GetReleaseRequest withTopLevelAsbiepId(String topLevelAsbiepId) {
        setTopLevelAsbiepId(topLevelAsbiepId);
        return this;
    }

    public String getReleaseNum() {
        return releaseNum;
    }

    public void setReleaseNum(String releaseNum) {
        this.releaseNum = releaseNum;
    }

    public GetReleaseRequest withReleaseNum(String releaseNum) {
        setReleaseNum(releaseNum);
        return this;
    }
}
