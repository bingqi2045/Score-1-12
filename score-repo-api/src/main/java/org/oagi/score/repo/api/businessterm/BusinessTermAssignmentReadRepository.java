package org.oagi.score.repo.api.businessterm;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.businessterm.model.GetAssignedBusinessTermListRequest;
import org.oagi.score.repo.api.businessterm.model.GetAssignedBusinessTermListResponse;

public interface BusinessTermAssignmentReadRepository {

     GetAssignedBusinessTermListResponse getBusinessTermAssignments(
            GetAssignedBusinessTermListRequest request) throws ScoreDataAccessException;

}
