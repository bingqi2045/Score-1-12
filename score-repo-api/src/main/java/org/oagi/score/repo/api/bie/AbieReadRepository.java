package org.oagi.score.repo.api.bie;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.bie.model.GetAbieListRequest;
import org.oagi.score.repo.api.bie.model.GetAbieListResponse;

public interface AbieReadRepository {

    GetAbieListResponse getAbieList(
            GetAbieListRequest request) throws ScoreDataAccessException;

}
