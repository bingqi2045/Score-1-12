package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

public class FindNextAsccpManifestRequest extends Request {

    private String asccpManifestId;

    private String nextReleaseId;

    public FindNextAsccpManifestRequest(ScoreUser requester) {
        super(requester);
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }

    public void setAsccpManifestId(String asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public FindNextAsccpManifestRequest withAsccpManifestId(String asccpManifestId) {
        setAsccpManifestId(asccpManifestId);
        return this;
    }

    public String getNextReleaseId() {
        return nextReleaseId;
    }

    public void setNextReleaseId(String nextReleaseId) {
        this.nextReleaseId = nextReleaseId;
    }

    public FindNextAsccpManifestRequest withNextReleaseId(String nextReleaseId) {
        setNextReleaseId(nextReleaseId);
        return this;
    }
}
