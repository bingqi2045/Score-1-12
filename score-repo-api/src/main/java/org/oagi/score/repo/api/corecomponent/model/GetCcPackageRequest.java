package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class GetCcPackageRequest extends Request {

    private String asccpManifestId;

    public GetCcPackageRequest(ScoreUser requester) {
        super(requester);
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }

    public void setAsccpManifestId(String asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public GetCcPackageRequest withAsccpManifestId(String asccpManifestId) {
        setAsccpManifestId(asccpManifestId);
        return this;
    }

}
