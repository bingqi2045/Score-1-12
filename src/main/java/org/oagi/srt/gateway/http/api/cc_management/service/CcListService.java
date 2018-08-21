package org.oagi.srt.gateway.http.api.cc_management.service;

import com.google.common.collect.Lists;
import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListTypes;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcListRepository;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class CcListService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CcListRepository repository;

    public PageResponse<CcList> getCcList(long releaseId, CcListTypes types, List<CcState> states,
                                          String filter, PageRequest pageRequest) {


        List<CcList> ccLists = getCoreComponents(releaseId, types, states, filter);
        Stream<CcList> ccListStream = ccLists.stream();

        Comparator<CcList> comparator = getComparator(pageRequest);
        if (comparator != null) {
            ccListStream = ccListStream.sorted(comparator);
        }

        PageResponse<CcList> pageResponse = getPageResponse(ccListStream.collect(Collectors.toList()), pageRequest);
        return pageResponse;
    }

    private List<CcList> getCoreComponents(long releaseId,
                                           CcListTypes types,
                                           List<CcState> states,
                                           String filter) {

        List<CcList> coreComponents = new ArrayList();
        if (types.isAcc()) {
            coreComponents.addAll(repository.getAccList(releaseId).stream()
                    .filter(statesFilter(states))
                    .filter(getDenFilter(filter))
                    .collect(Collectors.toList()));
        }

        if (types.isAscc()) {
            coreComponents.addAll(repository.getAsccList(releaseId).stream()
                    .filter(statesFilter(states))
                    .filter(getDenFilter(filter))
                    .collect(Collectors.toList()));
        }

        if (types.isBcc()) {
            coreComponents.addAll(repository.getBccList(releaseId).stream()
                    .filter(statesFilter(states))
                    .filter(getDenFilter(filter))
                    .collect(Collectors.toList()));
        }

        if (types.isAsccp()) {
            coreComponents.addAll(repository.getAsccpList(releaseId).stream()
                    .filter(statesFilter(states))
                    .filter(getDenFilter(filter))
                    .collect(Collectors.toList()));
        }

        if (types.isBccp()) {
            coreComponents.addAll(repository.getBccpList(releaseId).stream()
                    .filter(statesFilter(states))
                    .filter(getDenFilter(filter))
                    .collect(Collectors.toList()));
        }
        return coreComponents;
    }

    private Predicate<CcList> statesFilter(List<CcState> states) {
        return coreComponent -> states.contains(coreComponent.getState());
    }

    private Predicate<CcList> getDenFilter(String filter) {
        if (!StringUtils.isEmpty(filter)) {
            List<String> filters = Arrays.asList(filter.toLowerCase().split(" "));

            return coreComponent -> {
                List<String> den = Arrays.asList(coreComponent.getDen().toLowerCase().split(" "));
                for (String partialFilter : filters) {
                    if (!den.contains(partialFilter)) {
                        return false;
                    }
                }
                return true;
            };
        } else {
            return coreComponent -> true;
        }
    }

    private Comparator<CcList> getComparator(PageRequest pageRequest) {
        Comparator<CcList> comparator = null;
        switch (pageRequest.getSortActive()) {
            case "type":
                comparator = Comparator.comparing(CcList::getType);
                break;

            case "den":
                comparator = Comparator.comparing(CcList::getDen);
                break;

            case "owner":
                comparator = Comparator.comparing(CcList::getOwner);
                break;

            case "state":
                comparator = Comparator.comparing(CcList::getState);
                break;

            case "revision":
                comparator = Comparator.comparing(CcList::getRevision);
                break;

            case "deprecated":
                comparator = Comparator.comparing(CcList::isDeprecated);
                break;

            case "lastUpdateUser":
                comparator = Comparator.comparing(CcList::getLastUpdateUser);
                break;

            case "lastUpdateTimestamp":
                comparator = Comparator.comparing(CcList::getLastUpdateTimestamp);
                break;
        }

        if (comparator != null) {
            switch (pageRequest.getSortDirection()) {
                case "desc":
                    comparator = comparator.reversed();
                    break;
            }
        }

        return comparator;
    }

    private PageResponse<CcList> getPageResponse(List<CcList> list, PageRequest page) {
        PageResponse pageResponse = new PageResponse();

        int pageIndex = page.getPageIndex();
        pageResponse.setPage(pageIndex);

        int pageSize = page.getPageSize();
        pageResponse.setSize(pageSize);

        pageResponse.setLength(list.size());

        if (pageIndex < 0 || pageSize <= 0) {
            pageResponse.setList(Collections.emptyList());
        } else {
            if (list.size() > pageSize) {
                list = Lists.partition(list, pageSize).get(pageIndex);
            }
            pageResponse.setList(list);
        }

        return pageResponse;
    }
}
