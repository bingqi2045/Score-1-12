package org.oagi.score.repo.api.impl.jooq;

import org.jooq.DSLContext;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.businesscontext.*;
import org.oagi.score.repo.api.corecomponent.seqkey.SeqKeyReadRepository;
import org.oagi.score.repo.api.corecomponent.seqkey.SeqKeyWriteRepository;
import org.oagi.score.repo.api.impl.jooq.businesscontext.*;
import org.oagi.score.repo.api.impl.jooq.corecomponent.JooqSeqKeyReadRepository;
import org.oagi.score.repo.api.impl.jooq.corecomponent.JooqSeqKeyWriteRepository;
import org.oagi.score.repo.api.impl.jooq.user.JooqScoreUserReadRepository;
import org.oagi.score.repo.api.user.ScoreUserReadRepository;

public class JooqScoreRepositoryFactory implements ScoreRepositoryFactory {

    private final DSLContext dslContext;

    public JooqScoreRepositoryFactory(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public final DSLContext getDslContext() {
        return dslContext;
    }

    @Override
    public ScoreUserReadRepository createScoreUserReadRepository() throws ScoreDataAccessException {
        return new JooqScoreUserReadRepository(this.dslContext);
    }

    @Override
    public ContextCategoryReadRepository createContextCategoryReadRepository() throws ScoreDataAccessException {
        return new JooqContextCategoryReadRepository(this.dslContext);
    }

    @Override
    public ContextCategoryWriteRepository createContextCategoryWriteRepository() throws ScoreDataAccessException {
        return new JooqContextCategoryWriteRepository(this.dslContext);
    }

    @Override
    public ContextSchemeReadRepository createContextSchemeReadRepository() throws ScoreDataAccessException {
        return new JooqContextSchemeReadRepository(this.dslContext);
    }

    @Override
    public ContextSchemeWriteRepository createContextSchemeWriteRepository() throws ScoreDataAccessException {
        return new JooqContextSchemeWriteRepository(this.dslContext);
    }

    @Override
    public BusinessContextReadRepository createBusinessContextReadRepository() throws ScoreDataAccessException {
        return new JooqBusinessContextReadRepository(this.dslContext);
    }

    @Override
    public BusinessContextWriteRepository createBusinessContextWriteRepository() throws ScoreDataAccessException {
        return new JooqBusinessContextWriteRepository(this.dslContext);
    }

    @Override
    public SeqKeyReadRepository createSeqKeyReadRepository() throws ScoreDataAccessException {
        return new JooqSeqKeyReadRepository(this.dslContext);
    }

    @Override
    public SeqKeyWriteRepository createSeqKeyWriteRepository() throws ScoreDataAccessException {
        return new JooqSeqKeyWriteRepository(this.dslContext);
    }
}
