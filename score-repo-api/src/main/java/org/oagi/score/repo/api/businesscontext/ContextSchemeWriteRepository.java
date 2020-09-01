package org.oagi.score.repo.api.businesscontext;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.businesscontext.model.CreateContextSchemeRequest;
import org.oagi.score.repo.api.businesscontext.model.CreateContextSchemeResponse;

public interface ContextSchemeWriteRepository {

    CreateContextSchemeResponse createContextScheme(
            CreateContextSchemeRequest request) throws ScoreDataAccessException;

}
