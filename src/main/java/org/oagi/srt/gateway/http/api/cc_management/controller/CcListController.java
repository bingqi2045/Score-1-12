package org.oagi.srt.gateway.http.api.cc_management.controller;

import com.google.common.collect.Lists;
import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListStates;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListTypes;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.service.CcListService;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility.getRevision;

@RestController
public class CcListController {

    @Autowired
    private CcListService service;

    @RequestMapping(value = "/core_component", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public PageResponse<CcList> getCcListByReleaseId(
            @RequestParam(name = "release_id", required = false) long releaseId,
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "types", required = false) String types,
            @RequestParam(name = "states", required = false) String states,
            @RequestParam(name = "sortActive") String sortActive,
            @RequestParam(name = "sortDirection") String sortDirection,
            @RequestParam(name = "pageIndex") int pageIndex,
            @RequestParam(name = "pageSize") int pageSize) {

        PageRequest pageRequest = new PageRequest();
        pageRequest.setSortActive(sortActive);
        pageRequest.setSortDirection(sortDirection);
        pageRequest.setPageIndex(pageIndex);
        pageRequest.setPageSize(pageSize);

        Stream<CcList> ccListStream = service.getCcListByReleaseId(releaseId,
                CcListTypes.fromString(types), CcListStates.fromString(states))
                .stream().filter(getFilter(filter));

        Comparator<CcList> comparator = getComparator(pageRequest);
        if (comparator != null) {
            ccListStream = ccListStream.sorted(comparator);
        }

        List<CcList> ccLists = ccListStream.collect(Collectors.toList());
        PageResponse<CcList> pageResponse = getPageResponse(ccLists, pageRequest);
        pageResponse.getList().forEach(e -> {
            e.setType(e.getType().toUpperCase());
        });

        return pageResponse;
    }

    private Predicate<CcList> getFilter(String filter) {
        if (!StringUtils.isEmpty(filter)) {
            List<String> filters = Arrays.asList(filter.toLowerCase().split(" "));

            return e -> {
                List<String> den = Arrays.asList(e.getDen().toLowerCase().split(" "));
                for (String partialFilter : filters) {
                    if (!den.contains(partialFilter)) {
                        return false;
                    }
                }
                return true;
            };
        } else {
            return e -> true;
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
                comparator = Comparator.comparing(e -> (CcState) e.getState());
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
