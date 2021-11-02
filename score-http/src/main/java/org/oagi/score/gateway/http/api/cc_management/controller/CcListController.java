package org.oagi.score.gateway.http.api.cc_management.controller;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.oagi.score.gateway.http.api.cc_management.data.*;
import org.oagi.score.gateway.http.api.cc_management.data.elasticsearch.CoreComponent;
import org.oagi.score.gateway.http.api.cc_management.service.CcListService;
import org.oagi.score.gateway.http.api.cc_management.service.ElasticsearchCcListService;
import org.oagi.score.service.common.data.CcState;
import org.oagi.score.service.common.data.PageRequest;
import org.oagi.score.service.common.data.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

@RestController
public class CcListController {

    static final int FUZINESS_DISTANCE = 2;

    @Autowired
    private CcListService service;

    @Autowired
    private ElasticsearchCcListService esCCListService;

    @RequestMapping(value = "/core_component_search", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<CcList> getCcListBySearch(
            @RequestParam(name = "releaseId") String releaseId,
            @RequestParam(name = "den", required = false) String den,
            @RequestParam(name = "definition", required = false) String definition,
            @RequestParam(name = "module", required = false) String module,
            @RequestParam(name = "types", required = false) String types,
            @RequestParam(name = "states", required = false) String states,
            @RequestParam(name = "deprecated", required = false) String deprecated,
            @RequestParam(name = "ownerLoginIds", required = false) String ownerLoginIds,
            @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
            @RequestParam(name = "updateStart", required = false) String updateStart,
            @RequestParam(name = "updateEnd", required = false) String updateEnd,
            @RequestParam(name = "componentTypes", required = false) String componentTypes,
            @RequestParam(name = "asccpTypes", required = false) String asccpTypes,
            @RequestParam(name = "excludes", required = false) String excludes,
            @RequestParam(name = "isBIEUsable", required = false) String isBIEUsable,
            @RequestParam(name = "commonlyUsed", required = false) String commonlyUsed,
            @RequestParam(name = "sortActive") String sortActive,
            @RequestParam(name = "sortDirection") String sortDirection,
            @RequestParam(name = "pageIndex") int pageIndex,
            @RequestParam(name = "pageSize") int pageSize) {

        BoolQueryBuilder filterQueryPart = QueryBuilders.boolQuery();
        final NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        filterQueryPart.filter(matchQuery("release_id", releaseId));
        List<String> selectedTypes = Arrays.asList(types.split(","))
                .stream().map(String::toUpperCase).collect(Collectors.toList());
        ;
        filterQueryPart.filter(termsQuery("type.keyword", selectedTypes));

        if (StringUtils.hasLength(states)) {
            List<String> stateStrings = new ArrayList<>(Arrays.asList(states.split(",")));
            filterQueryPart.filter(termsQuery("state.keyword", stateStrings));
        }
        if (StringUtils.hasLength(deprecated)) {
            filterQueryPart.filter(matchQuery("deprecated", deprecated));
        }
        if (StringUtils.hasLength(ownerLoginIds)) {
            List<String> owners = Arrays.asList(ownerLoginIds.split(","));
            filterQueryPart.filter(termsQuery("owner.keyword", owners));
        }
        if (StringUtils.hasLength(updaterLoginIds)) {
            List<String> updaters = Arrays.asList(updaterLoginIds.split(","));
            filterQueryPart.filter(termsQuery("updater.keyword", updaters));
        }
        if (StringUtils.hasLength(den)) {
            if (den.startsWith("\"") && den.endsWith("\"")) {
                filterQueryPart.filter(termQuery("den.keyword", den.replace("\"", "")));
            } else {
                queryBuilder.withQuery(matchQuery("den", den).fuzziness(FUZINESS_DISTANCE));
            }
        }
        if (StringUtils.hasLength(definition)) {
            queryBuilder.withQuery(matchQuery("definition", definition));
        }
        if (StringUtils.hasLength(module)) {
            queryBuilder.withQuery(matchQuery("module", module));
        }
        if (StringUtils.hasLength(componentTypes)) {
            List<String> componentTypesList = Arrays.asList(componentTypes.split(","));
            filterQueryPart.filter(termsQuery("oagis_component_type.keyword", componentTypesList));
        }
        if (StringUtils.hasLength(asccpTypes)) {
            List<String> asccpTypesList = Arrays.asList(asccpTypes.split(","));
            filterQueryPart.filter(termsQuery("asccp_type.keyword", asccpTypesList));
        }
        if (StringUtils.hasLength(excludes)) {
            List<String> excludesList = Arrays.asList(excludes.split(","));
            filterQueryPart.filter(boolQuery().mustNot(termsQuery("manifest_id", excludesList)));
        }
        if (StringUtils.hasLength(updateStart)) {
            Date startDate = new Date(Long.parseLong(updateStart));
            filterQueryPart.filter(rangeQuery("last_update_timestamp").gte(startDate));
        }
        if (StringUtils.hasLength(updateEnd)) {
            Date endDate = new Date(Long.parseLong(updateEnd));
            filterQueryPart.filter(rangeQuery("last_update_timestamp").lte(endDate));
        }

        Sort sort;
        Pageable pageable;
        if (StringUtils.hasLength(sortActive)) {
            if (sortDirection.equals("desc")) {
                sort = Sort.by(camelToSnake(sortActive)).descending();
            } else {
                sort = Sort.by(camelToSnake(sortActive)).ascending();
            }
            pageable = org.springframework.data.domain.PageRequest.of(pageIndex, pageSize, sort);
        } else {
            pageable = org.springframework.data.domain.PageRequest.of(pageIndex, pageSize);
        }
        final Query searchQuery = queryBuilder
                .withPageable(pageable)
                .withFilter(filterQueryPart)
                .build();
        SearchPage<CoreComponent> esResponse = esCCListService.getCcListES(searchQuery);

        PageResponse<CcList> ccListPageResponse = new PageResponse<CcList>();
        ccListPageResponse.setList(esResponse.stream().map(cc ->
                new CcList(CcType.valueOf(cc.getContent().getType()),
                        cc.getContent().getManifest_id(),
                        cc.getContent().getGuid(),
                        cc.getContent().getDen(),
                        cc.getContent().getDefinition(),
                        cc.getContent().getModule(),
                        cc.getContent().getName(),
                        cc.getContent().getDefinition_source(),
                        Optional.ofNullable(cc.getContent().getOagis_component_type()),
                        cc.getContent().getDt_type(),
                        cc.getContent().getOwner(),
                        CcState.valueOf(cc.getContent().getState()),
                        cc.getContent().getRevision_num(),
                        Boolean.parseBoolean(cc.getContent().getDeprecated()),
                        cc.getContent().getUpdater(),
                        getDateFromString(cc.getContent().getLast_update_timestamp()),
                        cc.getContent().getRelease_num(),
                        cc.getContent().getComponent_id(),
                        Boolean.parseBoolean(cc.getContent().getOwned_by_developer()))
        ).collect(Collectors.toList()));
        ccListPageResponse.setPage(esResponse.getNumber());
        ccListPageResponse.setSize(esResponse.getSize());
        ccListPageResponse.setLength((int) esResponse.getTotalElements());

        return ccListPageResponse;
    }

    private Date getDateFromString(String timeString) {
        DateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            return dtFormat.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    String camelToSnake(String str) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";

        str = str.replaceAll(regex, replacement).toLowerCase();
        return str;
    }

    @RequestMapping(value = "/core_component", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<CcList> getCcList(
            @RequestParam(name = "releaseId") BigInteger releaseId,
            @RequestParam(name = "den", required = false) String den,
            @RequestParam(name = "definition", required = false) String definition,
            @RequestParam(name = "module", required = false) String module,
            @RequestParam(name = "types", required = false) String types,
            @RequestParam(name = "states", required = false) String states,
            @RequestParam(name = "deprecated", required = false) String deprecated,
            @RequestParam(name = "ownerLoginIds", required = false) String ownerLoginIds,
            @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
            @RequestParam(name = "updateStart", required = false) String updateStart,
            @RequestParam(name = "updateEnd", required = false) String updateEnd,
            @RequestParam(name = "componentTypes", required = false) String componentTypes,
            @RequestParam(name = "asccpTypes", required = false) String asccpTypes,
            @RequestParam(name = "excludes", required = false) String excludes,
            @RequestParam(name = "isBIEUsable", required = false) String isBIEUsable,
            @RequestParam(name = "commonlyUsed", required = false) String commonlyUsed,
            @RequestParam(name = "sortActive") String sortActive,
            @RequestParam(name = "sortDirection") String sortDirection,
            @RequestParam(name = "pageIndex") int pageIndex,
            @RequestParam(name = "pageSize") int pageSize) {

        CcListRequest request = new CcListRequest();

        request.setReleaseId(releaseId);
        request.setTypes(CcListTypes.fromString(types));
        if (StringUtils.hasLength(states)) {
            List<String> stateStrings = new ArrayList<>(Arrays.asList(states.split(",")));
            request.setStates(stateStrings.stream()
                    .map(e -> CcState.valueOf(e.trim())).collect(Collectors.toList()));
        }
        if (StringUtils.hasLength(deprecated)) {
            if ("true".equalsIgnoreCase(deprecated.toLowerCase())) {
                request.setDeprecated(true);
            } else if ("false".equalsIgnoreCase(deprecated.toLowerCase())) {
                request.setDeprecated(false);
            }
        }
        if (StringUtils.hasLength(isBIEUsable)) {
            if ("true".equalsIgnoreCase(isBIEUsable.toLowerCase())) {
                request.setIsBIEUsable(true);
            } else if ("false".equalsIgnoreCase(isBIEUsable.toLowerCase())) {
                request.setIsBIEUsable(false);
            }
        }
        if (StringUtils.hasLength(commonlyUsed)) {
            if ("true".equalsIgnoreCase(commonlyUsed.toLowerCase())) {
                request.setCommonlyUsed(true);
            } else if ("false".equalsIgnoreCase(commonlyUsed.toLowerCase())) {
                request.setCommonlyUsed(false);
            }
        }
        request.setOwnerLoginIds(!StringUtils.hasLength(ownerLoginIds) ? Collections.emptyList() :
                Arrays.asList(ownerLoginIds.split(",")).stream().map(e -> e.trim()).filter(e -> StringUtils.hasLength(e)).collect(Collectors.toList()));
        request.setUpdaterLoginIds(!StringUtils.hasLength(updaterLoginIds) ? Collections.emptyList() :
                Arrays.asList(updaterLoginIds.split(",")).stream().map(e -> e.trim()).filter(e -> StringUtils.hasLength(e)).collect(Collectors.toList()));
        request.setDen(den);
        request.setDefinition(definition);
        request.setModule(module);
        request.setComponentTypes(componentTypes);
        request.setAsccpTypes(!StringUtils.hasLength(asccpTypes) ? Collections.emptyList() :
                Arrays.asList(asccpTypes.split(",")).stream().map(e -> e.trim()).filter(e -> StringUtils.hasLength(e)).collect(Collectors.toList()));
        request.setExcludes(!StringUtils.hasLength(excludes) ? Collections.emptyList() :
                Arrays.asList(excludes.split(",")).stream().map(e -> e.trim()).filter(e -> StringUtils.hasLength(e)).collect(Collectors.toList()));

        if (StringUtils.hasLength(updateStart)) {
            request.setUpdateStartDate(new Date(Long.valueOf(updateStart)));
        }
        if (StringUtils.hasLength(updateEnd)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(updateEnd));
            calendar.add(Calendar.DATE, 1);
            request.setUpdateEndDate(calendar.getTime());
        }

        PageRequest pageRequest = new PageRequest();
        pageRequest.setSortActive(sortActive);
        pageRequest.setSortDirection(sortDirection);
        pageRequest.setPageIndex(pageIndex);
        pageRequest.setPageSize(pageSize);
        request.setPageRequest(pageRequest);

        return service.getCcList(request);
    }

    @RequestMapping(value = "/core_component/{type}/{manifestId:[\\d]+}/transfer_ownership",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity transferOwnership(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                            @PathVariable("type") String type,
                                            @PathVariable("manifestId") BigInteger manifestId,
                                            @RequestBody Map<String, String> request) {
        String targetLoginId = request.get("targetLoginId");
        service.transferOwnership(user, type, manifestId, targetLoginId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/core_component/transfer_ownership/multiple",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity transferOwnership(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                            @RequestBody CcTransferOwnerShipListRequest request) {
        service.transferOwnershipList(user, request);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/core_component/state/multiple",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateCcState(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                        @RequestBody CcUpdateStateListRequest request) {
        if (request.getAction().equals("Restore")) {
            service.restoreCcs(user, request);
        } else if (request.getAction().equals("Delete")) {
            service.deleteCcs(user, request);
        } else {
            service.updateStateCcs(user, request);
        }

        return ResponseEntity.noContent().build();
    }

}
