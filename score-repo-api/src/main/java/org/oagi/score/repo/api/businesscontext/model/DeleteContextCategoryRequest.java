package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.base.ScoreUser;

import java.math.BigInteger;

public class DeleteContextCategoryRequest extends Request {

    private BigInteger contextCategoryId;

    public DeleteContextCategoryRequest(ScoreUser requester) {
        super(requester);
    }

    public BigInteger getContextCategoryId() {
        return contextCategoryId;
    }

    public void setContextCategoryId(BigInteger contextCategoryId) {
        this.contextCategoryId = contextCategoryId;
    }

}
