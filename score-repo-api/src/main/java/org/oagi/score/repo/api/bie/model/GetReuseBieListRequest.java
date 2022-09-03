package org.oagi.score.repo.api.bie.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class GetReuseBieListRequest extends Request {

    private String topLevelAsbiepId;

    private boolean reusedBie;

    public GetReuseBieListRequest(ScoreUser requester) {
        super(requester);
    }

    public String getTopLevelAsbiepId() {
        return topLevelAsbiepId;
    }

    public void setTopLevelAsbiepId(String topLevelAsbiepId, boolean reusedBie) {
        this.topLevelAsbiepId = topLevelAsbiepId;
        this.reusedBie = reusedBie;
    }

    public GetReuseBieListRequest withTopLevelAsbiepId(String topLevelAsbiepId, boolean reusedBie) {
        setTopLevelAsbiepId(topLevelAsbiepId, reusedBie);
        return this;
    }

    public boolean isReusedBie() {
        return reusedBie;
    }
}
