package org.oagi.srt.gateway.http.api.cc_management.service;

import org.oagi.srt.gateway.http.api.cc_management.data.ACC;
import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListTypes;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.repository.CoreComponentRepository;
import org.oagi.srt.gateway.http.api.user_management.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility.getLatestEntity;
import static org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility.getRevision;

@Service
@Transactional(readOnly = true)
public class CcListService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CoreComponentRepository repository;

    @Autowired
    private UserRepository userRepository;

    private Predicate<CcList> statesFilter(List<CcState> states) {
        return e -> states.contains(e.getState());
    }

    public List<CcList> getCcListByReleaseId(long releaseId, CcListTypes types, List<CcState> states) {
        List<CcList> ccLists = new ArrayList();
        Map<Long, String> usernameMap = userRepository.getUsernameMap();

        Predicate<CcList> statesFilter = statesFilter(states);

        if (types.isAcc()) {
            List<ACC> accList = repository.getAccList();
            List<CcList> accCcList = accList.stream().map(acc -> {
                CcList ccList = new CcList();
                ccList.setType("ACC");
                ccList.setId(acc.getAccId());
                ccList.setGuid(acc.getGuid());
                ccList.setDen(acc.getDen());
                ccList.setRowState(acc.getState());
                ccList.setDeprecated(acc.isDeprecated());
                ccList.setCurrentId(acc.getCurrentAccId());
                ccList.setLastUpdateTimestamp(acc.getLastUpdateTimestamp());
                ccList.setOwner(usernameMap.get(acc.getOwnerUserId()));
                ccList.setLastUpdateUser(usernameMap.get(acc.getLastUpdatedBy()));

                ccList.setReleaseId(acc.getReleaseId());
                ccList.setRevisionNum(acc.getRevisionNum());
                ccList.setRevisionTrackingNum(acc.getRevisionTrackingNum());
                ccList.setRevisionAction(acc.getRevisionAction());

                return ccList;
            }).collect(Collectors.toList());
            ccLists.addAll(arrangeByReleaseId(releaseId, accCcList).stream()
                    .filter(statesFilter).collect(Collectors.toList()));
        }
        if (types.isAsccp()) {
            ccLists.addAll(arrangeByReleaseId(releaseId, repository.getAsccpList().stream().map(asccp -> {
                CcList ccList = new CcList();
                ccList.setType("ASCCP");
                ccList.setId(asccp.getAsccpId());
                ccList.setGuid(asccp.getGuid());
                ccList.setDen(asccp.getDen());
                ccList.setRowState(asccp.getState());
                ccList.setDeprecated(asccp.isDeprecated());
                ccList.setCurrentId(asccp.getCurrentAsccpId());
                ccList.setLastUpdateTimestamp(asccp.getLastUpdateTimestamp());
                ccList.setOwner(usernameMap.get(asccp.getOwnerUserId()));
                ccList.setLastUpdateUser(usernameMap.get(asccp.getLastUpdatedBy()));

                ccList.setReleaseId(asccp.getReleaseId());
                ccList.setRevisionNum(asccp.getRevisionNum());
                ccList.setRevisionTrackingNum(asccp.getRevisionTrackingNum());
                ccList.setRevisionAction(asccp.getRevisionAction());

                return ccList;
            }).collect(Collectors.toList())).stream()
                    .filter(statesFilter).collect(Collectors.toList()));
        }
        if (types.isBccp()) {
            ccLists.addAll(arrangeByReleaseId(releaseId, repository.getBccpList().stream().map(bccp -> {
                CcList ccList = new CcList();
                ccList.setType("BCCP");
                ccList.setId(bccp.getBccpId());
                ccList.setGuid(bccp.getGuid());
                ccList.setDen(bccp.getDen());
                ccList.setRowState(bccp.getState());
                ccList.setDeprecated(bccp.isDeprecated());
                ccList.setCurrentId(bccp.getCurrentBccpId());
                ccList.setLastUpdateTimestamp(bccp.getLastUpdateTimestamp());
                ccList.setOwner(usernameMap.get(bccp.getOwnerUserId()));
                ccList.setLastUpdateUser(usernameMap.get(bccp.getLastUpdatedBy()));

                ccList.setReleaseId(bccp.getReleaseId());
                ccList.setRevisionNum(bccp.getRevisionNum());
                ccList.setRevisionTrackingNum(bccp.getRevisionTrackingNum());
                ccList.setRevisionAction(bccp.getRevisionAction());

                return ccList;
            }).collect(Collectors.toList())).stream()
                    .filter(statesFilter).collect(Collectors.toList()));
        }
        if (types.isAscc()) {
            ccLists.addAll(arrangeByReleaseId(releaseId, repository.getAsccList().stream().map(ascc -> {
                CcList ccList = new CcList();
                ccList.setType("ASCC");
                ccList.setId(ascc.getAsccId());
                ccList.setGuid(ascc.getGuid());
                ccList.setDen(ascc.getDen());
                ccList.setRowState(ascc.getState());
                ccList.setDeprecated(ascc.isDeprecated());
                ccList.setCurrentId(ascc.getCurrentAsccId());
                ccList.setLastUpdateTimestamp(ascc.getLastUpdateTimestamp());
                ccList.setOwner(usernameMap.get(ascc.getOwnerUserId()));
                ccList.setLastUpdateUser(usernameMap.get(ascc.getLastUpdatedBy()));

                ccList.setReleaseId(ascc.getReleaseId());
                ccList.setRevisionNum(ascc.getRevisionNum());
                ccList.setRevisionTrackingNum(ascc.getRevisionTrackingNum());
                ccList.setRevisionAction(ascc.getRevisionAction());

                return ccList;
            }).collect(Collectors.toList())).stream()
                    .filter(statesFilter).collect(Collectors.toList()));
        }
        if (types.isBcc()) {
            ccLists.addAll(arrangeByReleaseId(releaseId, repository.getBccList().stream().map(bcc -> {
                CcList ccList = new CcList();
                ccList.setType("BCC");
                ccList.setId(bcc.getBccId());
                ccList.setGuid(bcc.getGuid());
                ccList.setDen(bcc.getDen());
                ccList.setRowState(bcc.getState());
                ccList.setDeprecated(bcc.isDeprecated());
                ccList.setCurrentId(bcc.getCurrentBccId());
                ccList.setLastUpdateTimestamp(bcc.getLastUpdateTimestamp());
                ccList.setOwner(usernameMap.get(bcc.getOwnerUserId()));
                ccList.setLastUpdateUser(usernameMap.get(bcc.getLastUpdatedBy()));

                ccList.setReleaseId(bcc.getReleaseId());
                ccList.setRevisionNum(bcc.getRevisionNum());
                ccList.setRevisionTrackingNum(bcc.getRevisionTrackingNum());
                ccList.setRevisionAction(bcc.getRevisionAction());

                return ccList;
            }).collect(Collectors.toList())).stream()
                    .filter(statesFilter).collect(Collectors.toList()));
        }

        return ccLists;
    }

    private List<CcList> arrangeByReleaseId(long releaseId, List<CcList> ccLists) {
        Map<String, List<CcList>> groupingByTypeAndGuid =
                ccLists.stream().collect(groupingBy(e -> e.getType() + "-" + e.getGuid()));

        Stream<CcList> entityStream;
        if (releaseId == 0L) {
            entityStream = groupingByTypeAndGuid.values().stream()
                    .map(e -> {
                        for (CcList ccList : e) {
                            if (ccList.getReleaseId() == null) {
                                return ccList;
                            }
                        }
                        return null;
                    });
        } else {
            entityStream = groupingByTypeAndGuid.values().stream()
                    .map(e -> getLatestEntity(releaseId, e));
        }

        Map<String, CcList> ccListMap = entityStream
                .collect(toMap(e -> e.getType() + "-" + e.getGuid(), Function.identity()));
        groupingByTypeAndGuid.entrySet().stream().forEach(entry -> {
            String revision = getRevision(releaseId, entry.getValue());
            ccListMap.get(entry.getKey()).setRevision(revision);
        });

        return ccListMap.values().stream().map(e -> {
            e.setState(CcState.valueOf(e.getRowState()));
            return e;
        }).collect(Collectors.toList());
    }
}
