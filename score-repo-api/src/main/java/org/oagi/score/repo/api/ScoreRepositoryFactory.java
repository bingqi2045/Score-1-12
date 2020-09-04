package org.oagi.score.repo.api;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.businesscontext.*;
import org.oagi.score.repo.api.corecomponent.seqkey.SeqKeyReadRepository;
import org.oagi.score.repo.api.corecomponent.seqkey.SeqKeyWriteRepository;
import org.oagi.score.repo.api.user.ScoreUserReadRepository;

public interface ScoreRepositoryFactory {

    ScoreUserReadRepository createScoreUserReadRepository() throws ScoreDataAccessException;

    ContextCategoryReadRepository createContextCategoryReadRepository() throws ScoreDataAccessException;
    ContextCategoryWriteRepository createContextCategoryWriteRepository() throws ScoreDataAccessException;

    ContextSchemeReadRepository createContextSchemeReadRepository() throws ScoreDataAccessException;
    ContextSchemeWriteRepository createContextSchemeWriteRepository() throws ScoreDataAccessException;

    BusinessContextReadRepository createBusinessContextReadRepository() throws ScoreDataAccessException;
    BusinessContextWriteRepository createBusinessContextWriteRepository() throws ScoreDataAccessException;

    SeqKeyReadRepository createSeqKeyReadRepository() throws ScoreDataAccessException;
    SeqKeyWriteRepository createSeqKeyWriteRepository() throws ScoreDataAccessException;

}
