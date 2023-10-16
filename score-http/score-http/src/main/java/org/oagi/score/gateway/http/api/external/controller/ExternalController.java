package org.oagi.score.gateway.http.api.external.controller;

import org.oagi.score.gateway.http.api.cc_management.data.*;
import org.oagi.score.gateway.http.api.cc_management.service.CcListService;
import org.oagi.score.gateway.http.api.external.service.ExternalService;
import org.oagi.score.gateway.http.api.module_management.data.ExportStandaloneSchemaResponse;
import org.oagi.score.gateway.http.helper.DeleteOnCloseFileSystemResource;
import org.oagi.score.service.common.data.CcState;
import org.oagi.score.service.common.data.PageRequest;
import org.oagi.score.service.common.data.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ExternalController {

    @Autowired
    private ExternalService service;

    @Autowired
    private CcListService ccService;


    @RequestMapping(value = "/ext/core_component", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<CcList> getCcList(
            @RequestParam(name = "releaseId") BigInteger releaseId,
            @RequestParam(name = "den", required = false) String den,
            @RequestParam(name = "definition", required = false) String definition,
            @RequestParam(name = "module", required = false) String module,
            @RequestParam(name = "types", required = false) String types,
            @RequestParam(name = "states", required = false) String states,
            @RequestParam(name = "deprecated", required = false) String deprecated,
            @RequestParam(name = "newComponent", required = false) String newComponent,
            @RequestParam(name = "ownerLoginIds", required = false) String ownerLoginIds,
            @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
            @RequestParam(name = "updateStart", required = false) String updateStart,
            @RequestParam(name = "updateEnd", required = false) String updateEnd,
            @RequestParam(name = "tags", required = false) String tags,
            @RequestParam(name = "namespaces", required = false) String namespaces,
            @RequestParam(name = "componentTypes", required = false) String componentTypes,
            @RequestParam(name = "dtTypes", required = false) String dtTypes,
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
        if (StringUtils.hasLength(newComponent)) {
            if ("true".equalsIgnoreCase(newComponent.toLowerCase())) {
                request.setNewComponent(true);
            } else if ("false".equalsIgnoreCase(newComponent.toLowerCase())) {
                request.setNewComponent(false);
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
        request.setOwnerLoginIds(!StringUtils.hasLength(ownerLoginIds) ? Collections.emptyList()
                : Arrays.asList(ownerLoginIds.split(",")).stream().map(e -> e.trim())
                        .filter(e -> StringUtils.hasLength(e)).collect(Collectors.toList()));
        request.setUpdaterLoginIds(!StringUtils.hasLength(updaterLoginIds) ? Collections.emptyList()
                : Arrays.asList(updaterLoginIds.split(",")).stream().map(e -> e.trim())
                        .filter(e -> StringUtils.hasLength(e)).collect(Collectors.toList()));
        request.setDen(den);
        request.setDefinition(definition);
        request.setModule(module);
        request.setTags(!StringUtils.hasLength(tags) ? Collections.emptyList()
                : Arrays.asList(tags.split(",")).stream().map(e -> e.trim()).filter(e -> StringUtils.hasLength(e))
                        .collect(Collectors.toList()));
        request.setNamespaces(!StringUtils.hasLength(namespaces) ? Collections.emptyList()
                : Arrays.asList(namespaces.split(",")).stream().map(e -> e.trim()).filter(e -> StringUtils.hasLength(e))
                        .map(e -> new BigInteger(e)).collect(Collectors.toList()));
        request.setComponentTypes(componentTypes);
        request.setDtTypes(!StringUtils.hasLength(dtTypes) ? Collections.emptyList()
                : Arrays.asList(dtTypes.split(",")).stream().map(e -> e.trim()).filter(e -> StringUtils.hasLength(e))
                        .collect(Collectors.toList()));
        if ((request.getTypes().isCdt() || request.getTypes().isBdt()) && request.getDtTypes().isEmpty()) {
            List<String> dtTypeList = new ArrayList<String>();
            if (request.getTypes().isCdt()) {
                dtTypeList.add("CDT");
            }
            if (request.getTypes().isBdt()) {
                dtTypeList.add("BDT");
            }
            request.setDtTypes(dtTypeList);
        }

        request.setAsccpTypes(!StringUtils.hasLength(asccpTypes) ? Collections.emptyList()
                : Arrays.asList(asccpTypes.split(",")).stream().map(e -> e.trim()).filter(e -> StringUtils.hasLength(e))
                        .collect(Collectors.toList()));
        request.setExcludes(!StringUtils.hasLength(excludes) ? Collections.emptyList()
                : Arrays.asList(excludes.split(",")).stream().map(e -> e.trim()).filter(e -> StringUtils.hasLength(e))
                        .collect(Collectors.toList()));

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

        return ccService.getCcListWithLastUpdatedAndSince(request);
    }

    /*
     * @RequestMapping(value =
     * "/ext/core_component/changes_in_release/{releaseId:[\\d]+}", method =
     * RequestMethod.GET,
     * produces = MediaType.APPLICATION_JSON_VALUE)
     * public CcChangesResponse getCcChanges(
     * 
     * @AuthenticationPrincipal AuthenticatedPrincipal user,
     * 
     * @PathVariable("releaseId") BigInteger releaseId) {
     * return service.getCcChanges(sessionService.asScoreUser(user), releaseId);
     * }
     */

    @RequestMapping(value = "/ext/core_component/export/standalone", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeleteOnCloseFileSystemResource> exportStandaloneSchema(
            @RequestParam(name = "asccpManifestIdList", required = true) String asccpManifestIdList) throws Exception {

        ExportStandaloneSchemaResponse response = service.exportStandaloneSchema(
                Arrays.stream(asccpManifestIdList.split(",")).map(e -> new BigInteger(e)).collect(Collectors.toList()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(
                        (response.getFilename().endsWith(".zip") ? "application/zip" : "application/xml")))
                .contentLength(response.getFile().length())
                .body(new DeleteOnCloseFileSystemResource(response.getFile()));
    }

    @RequestMapping(value = "/ext/releases", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getReleases() {
        return service.getReleases();
    }

    @RequestMapping(value = "/ext/latest_release", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getLatestRelease() {
        return service.getLatestRelease();
    }

    @RequestMapping(value = "/ext/auth", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> auth() {
        return ResponseEntity.noContent().build();
    }
}