package org.oagi.score.repo.api.agency;

import org.oagi.score.repo.api.agency.model.AgencyIdList;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.corecomponent.model.CcState;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public interface AgencyIdListWriteRepository {
    String createAgencyIdList(ScoreUser user, String releaseId, String basedAgencyIdListManifestId) throws ScoreDataAccessException;
    AgencyIdList updateAgencyIdListProperty(ScoreUser user, AgencyIdList agencyIdList) throws ScoreDataAccessException;
    void updateAgencyIdListState(ScoreUser user, String agencyIdListManifestId, CcState toState) throws ScoreDataAccessException;
    void reviseAgencyIdList(ScoreUser user, String agencyIdListManifestId) throws ScoreDataAccessException;
    void cancelAgencyIdList(ScoreUser user, String agencyIdListManifestId) throws ScoreDataAccessException;
    void transferOwnershipAgencyIdList(ScoreUser user, String agencyIdListManifestId, String targetLoginId) throws ScoreDataAccessException;
}
