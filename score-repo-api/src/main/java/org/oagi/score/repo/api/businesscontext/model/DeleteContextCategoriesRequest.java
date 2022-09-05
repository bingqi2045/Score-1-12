package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class DeleteContextCategoriesRequest extends Request {

    private Collection<String> contextCategoryIds;

    public DeleteContextCategoriesRequest(ScoreUser requester) {
        super(requester);
    }

    public Collection<String> getContextCategoryIds() {
        return (contextCategoryIds == null) ? Collections.emptyList() : contextCategoryIds;
    }

    public void addContextCategoryId(String contextCategoryId) {
        if (contextCategoryId == null) {
            throw new IllegalArgumentException();
        }
        
        if (this.contextCategoryIds == null) {
            this.contextCategoryIds = new ArrayList();
        }

        this.contextCategoryIds.add(contextCategoryId);
    }

    public void setContextCategoryIds(Collection<String> contextCategoryIds) {
        this.contextCategoryIds = contextCategoryIds;
    }
}
