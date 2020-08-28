package org.oagi.score.repo.api;

import org.oagi.score.repo.api.businesscontext.*;

public interface ScoreRepositoryFactory {

    ContextCategoryReadRepository createContextCategoryReadRepository();
    ContextCategoryReadRepository createContextCategoryWriteRepository();

    ContextSchemeReadRepository createContextSchemeReadRepository();
    ContextSchemeWriteRepository createContextSchemeWriteRepository();

    BusinessContextReadRepository createBusinessContextReadRepository();
    BusinessContextWriteRepository createBusinessContextWriteRepository();

}
