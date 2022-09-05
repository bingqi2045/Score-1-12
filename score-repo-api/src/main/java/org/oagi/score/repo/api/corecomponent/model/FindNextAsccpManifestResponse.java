package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.base.Response;

public class FindNextAsccpManifestResponse extends Response {

    private final String nextAsccpManifestId;
    private final String releaseNum;

    public FindNextAsccpManifestResponse(String nextAsccpManifestId, String releaseNum) {
        this.nextAsccpManifestId = nextAsccpManifestId;
        this.releaseNum = releaseNum;
    }

    public String getNextAsccpManifestId() {
        return nextAsccpManifestId;
    }

    public String getReleaseNum() {
        return releaseNum;
    }
}
