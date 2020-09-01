package org.oagi.score.repo.api.businesscontext;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.businesscontext.model.GetContextSchemeRequest;
import org.oagi.score.repo.api.businesscontext.model.GetContextSchemeResponse;

public interface ContextSchemeReadRepository {

    GetContextSchemeResponse getContextScheme(
            GetContextSchemeRequest request) throws ScoreDataAccessException;

}
