package org.oagi.srt.gateway.http.api.cc_management.service;

import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListTypes;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CcListService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CcListRepository repository;

    private Predicate<CcList> statesFilter(List<CcState> states) {
        return e -> states.contains(e.getState());
    }

    public List<CcList> getCcListByReleaseId(long releaseId, CcListTypes types, List<CcState> states) {
        List<CcList> ccLists = new ArrayList();

        Predicate<CcList> statesFilter = statesFilter(states);

        if (types.isAcc()) {
            ccLists.addAll(repository.getAccCcList(releaseId).stream()
                    .filter(statesFilter).collect(Collectors.toList()));
        }
        if (types.isAsccp()) {
            ccLists.addAll(repository.getAsccpCcList(releaseId).stream()
                    .filter(statesFilter).collect(Collectors.toList()));
        }
        if (types.isBccp()) {
            ccLists.addAll(repository.getBccpCcList(releaseId).stream()
                    .filter(statesFilter).collect(Collectors.toList()));
        }
        if (types.isAscc()) {
            ccLists.addAll(repository.getAsccCcList(releaseId).stream()
                    .filter(statesFilter).collect(Collectors.toList()));
        }
        if (types.isBcc()) {
            ccLists.addAll(repository.getBccCcList(releaseId).stream()
                    .filter(statesFilter).collect(Collectors.toList()));
        }

        return ccLists;
    }
}
