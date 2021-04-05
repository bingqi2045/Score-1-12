package org.oagi.score.gateway.http.api.agency_id_management.service;

import org.jooq.DSLContext;
import org.oagi.score.export.model.AgencyId;
import org.oagi.score.gateway.http.api.agency_id_management.data.SimpleAgencyIdListValue;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.agency.model.AgencyIdList;
import org.oagi.score.repo.api.agency.model.GetAgencyIdListListRequest;
import org.oagi.score.repo.api.agency.model.GetAgencyIdListListResponse;
import org.oagi.score.repo.api.corecomponent.model.CcState;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.user.model.ScoreUser;
import org.oagi.score.service.log.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AgencyIdService {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    public List<SimpleAgencyIdListValue> getSimpleAgencyIdListValues() {
        return dslContext.select(Tables.AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID,
                Tables.AGENCY_ID_LIST_VALUE.NAME)
                .from(Tables.AGENCY_ID_LIST_VALUE)
                .fetchStreamInto(SimpleAgencyIdListValue.class)
                .sorted(Comparator.comparing(SimpleAgencyIdListValue::getName))
                .collect(Collectors.toList());
    }

    public AgencyIdList getAgencyIdListDetail(BigInteger manifestId) {
        return scoreRepositoryFactory.createAgencyIdListReadRepository().getAgencyIdList(manifestId);
    }

    public GetAgencyIdListListResponse getAgencyIdListList(GetAgencyIdListListRequest request) {
        return scoreRepositoryFactory.createAgencyIdListReadRepository().getAgencyIdListList(request);
    }

    @Transactional
    public BigInteger createAgencyIdList(ScoreUser user, BigInteger releaseId) {
        return scoreRepositoryFactory.createAgencyIdListWriteRepository().createAgencyIdList(user, releaseId);
    }

    @Transactional
    public AgencyIdList updateAgencyIdListProperty(ScoreUser user, AgencyIdList agencyIdList) {
        return scoreRepositoryFactory.createAgencyIdListWriteRepository().updateAgencyIdListProperty(user, agencyIdList);
    }

    @Transactional
    public void updateAgencyIdListState(ScoreUser user, BigInteger agencyIdListManifestId, CcState toState) {
        scoreRepositoryFactory.createAgencyIdListWriteRepository().updateAgencyIdListState(user, agencyIdListManifestId, toState);
    }
}
