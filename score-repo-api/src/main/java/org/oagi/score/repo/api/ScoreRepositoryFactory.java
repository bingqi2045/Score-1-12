package org.oagi.score.repo.api;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.businesscontext.*;

public interface ScoreRepositoryFactory {

    ContextCategoryReadRepository createContextCategoryReadRepository() throws ScoreDataAccessException;
    ContextCategoryWriteRepository createContextCategoryWriteRepository() throws ScoreDataAccessException;

    ContextSchemeReadRepository createContextSchemeReadRepository() throws ScoreDataAccessException;
    ContextSchemeWriteRepository createContextSchemeWriteRepository() throws ScoreDataAccessException;

    BusinessContextReadRepository createBusinessContextReadRepository() throws ScoreDataAccessException;
    BusinessContextWriteRepository createBusinessContextWriteRepository() throws ScoreDataAccessException;

}
