package org.oagi.score.repo.api.businessterm;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.businesscontext.model.*;

public interface BusinessTermWriteRepository {

    CreateBusinessContextResponse createBusinessContext(
            CreateBusinessContextRequest request) throws ScoreDataAccessException;
//
//    UpdateBusinessContextResponse updateBusinessContext(
//            UpdateBusinessContextRequest request) throws ScoreDataAccessException;
//
//    DeleteBusinessContextResponse deleteBusinessContext(
//            DeleteBusinessContextRequest request) throws ScoreDataAccessException;
    
}
