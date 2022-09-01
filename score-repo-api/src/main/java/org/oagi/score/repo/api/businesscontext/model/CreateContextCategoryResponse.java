package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Response;

import java.math.BigInteger;

public class CreateContextCategoryResponse extends Response {

    private final String contextCategoryId;

    public CreateContextCategoryResponse(String contextCategoryId) {
        this.contextCategoryId = contextCategoryId;
    }

    public String getContextCategoryId() {
        return contextCategoryId;
    }

}
