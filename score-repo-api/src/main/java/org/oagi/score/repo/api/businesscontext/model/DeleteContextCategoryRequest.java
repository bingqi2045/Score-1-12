package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DeleteContextCategoryRequest extends Request {

    private List<String> contextCategoryIdList = Collections.emptyList();

    public DeleteContextCategoryRequest(ScoreUser requester) {
        super(requester);
    }

    public List<String> getContextCategoryIdList() {
        return contextCategoryIdList;
    }

    public void setContextCategoryId(String contextCategoryId) {
        if (contextCategoryId != null) {
            this.contextCategoryIdList = Arrays.asList(contextCategoryId);
        }
    }

    public DeleteContextCategoryRequest withContextCategoryId(String contextCategoryId) {
        this.setContextCategoryId(contextCategoryId);
        return this;
    }

    public void setContextCategoryIdList(List<String> contextCategoryIdList) {
        if (contextCategoryIdList != null) {
            this.contextCategoryIdList = contextCategoryIdList;
        }
    }

    public DeleteContextCategoryRequest withContextCategoryIdList(List<String> contextCategoryIdList) {
        this.setContextCategoryIdList(contextCategoryIdList);
        return this;
    }

}
