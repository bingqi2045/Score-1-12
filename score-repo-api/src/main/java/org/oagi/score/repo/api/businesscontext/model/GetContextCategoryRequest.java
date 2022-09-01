package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class GetContextCategoryRequest extends Request {

    private String contextCategoryId;

    public GetContextCategoryRequest(ScoreUser requester) {
        super(requester);
    }

    public String getContextCategoryId() {
        return contextCategoryId;
    }

    public void setContextCategoryId(String contextCategoryId) {
        this.contextCategoryId = contextCategoryId;
    }

    public GetContextCategoryRequest withContextCategoryId(String contextCategoryId) {
        this.setContextCategoryId(contextCategoryId);
        return this;
    }
}
