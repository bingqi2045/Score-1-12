package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Response;

import java.util.Arrays;
import java.util.Collection;

public class DeleteContextCategoriesResponse extends Response {

    private final Collection<String> contextCategoryIds;

    public DeleteContextCategoriesResponse(String contextCategoryId) {
        this(Arrays.asList(contextCategoryId));
    }

    public DeleteContextCategoriesResponse(String... contextCategoryIds) {
        this(Arrays.asList(contextCategoryIds));
    }

    public DeleteContextCategoriesResponse(Collection<String> contextCategoryIds) {
        this.contextCategoryIds = contextCategoryIds;
    }

    public Collection<String> getContextCategoryIds() {
        return contextCategoryIds;
    }
}
