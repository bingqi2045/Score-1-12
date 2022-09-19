package org.oagi.score.repo.api.bie.model;

import org.oagi.score.repo.api.base.Response;

public class CreateBieResponse extends Response {

    private String topLevelAsbiepId;

    public CreateBieResponse(String topLevelAsbiepId) {
        this.topLevelAsbiepId = topLevelAsbiepId;
    }

    public String getTopLevelAsbiepId() {
        return topLevelAsbiepId;
    }

}
