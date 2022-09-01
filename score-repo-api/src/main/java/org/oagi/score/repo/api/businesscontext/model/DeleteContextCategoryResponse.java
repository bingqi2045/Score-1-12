package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Response;

import java.math.BigInteger;
import java.util.List;

public class DeleteContextCategoryResponse extends Response {

    private final List<String> contextCategoryIdList;

    public DeleteContextCategoryResponse(List<String> contextCategoryIdList) {
        this.contextCategoryIdList = contextCategoryIdList;
    }

    public List<String> getContextCategoryIdList() {
        return contextCategoryIdList;
    }

    public boolean contains(String contextCategoryId) {
        return this.contextCategoryIdList.contains(contextCategoryId);
    }

    public boolean containsAll(List<String> contextCategoryIdList) {
        for (String contextCategoryId : contextCategoryIdList) {
            if (!this.contextCategoryIdList.contains(contextCategoryId)) {
                return false;
            }
        }
        return true;
    }
}
