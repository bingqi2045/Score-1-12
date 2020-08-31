package org.oagi.score.repo.api.businesscontext;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.businesscontext.model.GetContextCategoryRequest;
import org.oagi.score.repo.api.businesscontext.model.GetContextCategoryResponse;
import org.oagi.score.repo.api.businesscontext.model.ListContextCategoryRequest;
import org.oagi.score.repo.api.businesscontext.model.ListContextCategoryResponse;

public interface ContextCategoryReadRepository {

    GetContextCategoryResponse getContextCategory(
            GetContextCategoryRequest request) throws ScoreDataAccessException;

    ListContextCategoryResponse listContextCategories(
            ListContextCategoryRequest request) throws ScoreDataAccessException;

}
