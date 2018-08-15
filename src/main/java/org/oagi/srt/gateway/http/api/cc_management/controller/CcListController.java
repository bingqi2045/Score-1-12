package org.oagi.srt.gateway.http.api.cc_management.controller;

import com.google.common.collect.Lists;
import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.service.CcListService;
import org.oagi.srt.gateway.http.api.common.data.Pagination;
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
import java.util.stream.Collectors;

@RestController
public class CcListController {

    @Autowired
    private CcListService service;

    @RequestMapping(value = "/core_component", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Pagination<CcList> getCcListByReleaseId(
            @RequestParam(name = "release_id", required = false) long releaseId,
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "sortActive") String sortActive,
            @RequestParam(name = "sortDirection") String sortDirection,
            @RequestParam(name = "pageIndex") int pageIndex,
            @RequestParam(name = "pageSize") int pageSize) {

        List<CcList> ccLists = service.getCcListByReleaseId(releaseId);

        if (!StringUtils.isEmpty(filter)) {
            List<String> filters = Arrays.asList(filter.toLowerCase().split(" "));

            ccLists = ccLists.stream()
                    .filter(e -> {
                        List<String> den = Arrays.asList(e.getDen().toLowerCase().split(" "));
                        for (String partialFilter : filters) {
                            if (!den.contains(partialFilter)) {
                                return false;
                            }
                        }
                        return true;
                    }).collect(Collectors.toList());
        }

        Comparator<CcList> comparator = null;
        switch (sortActive) {
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
            switch (sortDirection) {
                case "desc":
                    comparator = comparator.reversed();
                    break;
            }

            ccLists = ccLists.stream().sorted(comparator).collect(Collectors.toList());
        }

        Pagination pagination = new Pagination();
        pagination.setPage(pageIndex);
        pagination.setSize(pageSize);
        pagination.setLength(ccLists.size());

        if (pageIndex < 0 || pageSize <= 0) {
            pagination.setList(Collections.emptyList());
        } else {
            if (ccLists.size() > pageSize) {
                ccLists = Lists.partition(ccLists, pageSize).get(pageIndex);
            }
            ccLists.stream().forEach(e -> {
                e.setType(e.getType().toUpperCase());
            });
            pagination.setList(ccLists);
        }

        return pagination;
    }

}
