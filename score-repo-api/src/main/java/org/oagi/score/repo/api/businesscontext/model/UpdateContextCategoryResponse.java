package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Response;

public class UpdateContextCategoryResponse extends Response {

    private final String contextCategoryId;
    private final boolean changed;

    public UpdateContextCategoryResponse(String contextCategoryId, boolean changed) {
        this.contextCategoryId = contextCategoryId;
        this.changed = changed;
    }

    public String getContextCategoryId() {
        return contextCategoryId;
    }

    public boolean isChanged() {
        return changed;
    }
}
