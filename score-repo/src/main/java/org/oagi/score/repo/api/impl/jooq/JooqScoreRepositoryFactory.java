package org.oagi.score.repo.api.impl.jooq;

import org.jooq.DSLContext;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.businesscontext.*;

public class JooqScoreRepositoryFactory implements ScoreRepositoryFactory {

    private final DSLContext dslContext;

    public JooqScoreRepositoryFactory(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public ContextCategoryReadRepository createContextCategoryReadRepository() {
        return null;
    }

    @Override
    public ContextCategoryReadRepository createContextCategoryWriteRepository() {
        return null;
    }

    @Override
    public ContextSchemeReadRepository createContextSchemeReadRepository() {
        return null;
    }

    @Override
    public ContextSchemeWriteRepository createContextSchemeWriteRepository() {
        return null;
    }

    @Override
    public BusinessContextReadRepository createBusinessContextReadRepository() {
        return null;
    }

    @Override
    public BusinessContextWriteRepository createBusinessContextWriteRepository() {
        return null;
    }
}
