package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class GetContextSchemeRequest extends Request {

    private String contextSchemeId;

    public GetContextSchemeRequest(ScoreUser requester) {
        super(requester);
    }

    public String getContextSchemeId() {
        return contextSchemeId;
    }

    public void setContextSchemeId(String contextSchemeId) {
        this.contextSchemeId = contextSchemeId;
    }

    public GetContextSchemeRequest withContextSchemeId(String contextSchemeId) {
        this.setContextSchemeId(contextSchemeId);
        return this;
    }
}
