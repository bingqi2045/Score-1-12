package org.oagi.score.repo.api.bie.model;

import org.oagi.score.repo.api.base.PaginationRequest;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class GetAbieListRequest extends PaginationRequest<Abie> {

    private BigInteger topLevelAsbiepId;

    public GetAbieListRequest(ScoreUser requester) {
        super(requester, Abie.class);
    }

    public BigInteger getTopLevelAsbiepId() {
        return topLevelAsbiepId;
    }

    public void setTopLevelAsbiepId(BigInteger topLevelAsbiepId) {
        this.topLevelAsbiepId = topLevelAsbiepId;
    }
}
