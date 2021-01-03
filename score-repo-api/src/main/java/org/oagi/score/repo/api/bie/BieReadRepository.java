package org.oagi.score.repo.api.bie;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.bie.model.GetBiePackageRequest;
import org.oagi.score.repo.api.bie.model.GetBiePackageResponse;

public interface BieReadRepository {

    GetBiePackageResponse getBiePackage(
            GetBiePackageRequest request) throws ScoreDataAccessException;

}
