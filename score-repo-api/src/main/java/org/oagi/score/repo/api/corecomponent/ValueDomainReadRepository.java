package org.oagi.score.repo.api.corecomponent;

import org.oagi.score.repo.api.agency.model.AgencyIdList;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.corecomponent.model.BdtPriRestri;
import org.oagi.score.repo.api.corecomponent.model.BdtScPriRestri;
import org.oagi.score.repo.api.corecomponent.model.CodeList;

import java.util.List;
import java.util.Map;

public interface ValueDomainReadRepository {

    List<CodeList> getCodeListList(
            String releaseId) throws ScoreDataAccessException;

    Map<String, BdtPriRestri> getBdtPriRestriMap(
            String releaseId) throws ScoreDataAccessException;

    Map<String, BdtScPriRestri> getBdtScPriRestriMap(
            String releaseId) throws ScoreDataAccessException;

    Map<String, List<BdtPriRestri>> getBdtPriRestriBdtIdMap(
            String releaseId) throws ScoreDataAccessException;

    Map<String, List<BdtScPriRestri>> getBdtScPriRestriBdtScIdMap(
            String releaseId) throws ScoreDataAccessException;

    List<AgencyIdList> getAgencyIdListList(
            String releaseId) throws ScoreDataAccessException;
}
