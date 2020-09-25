package org.oagi.score.gateway.http.api.code_list_management.controller;

import org.oagi.score.gateway.http.api.cc_management.data.CcCreateResponse;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.gateway.http.api.code_list_management.data.*;
import org.oagi.score.gateway.http.api.code_list_management.service.CodeListService;
import org.oagi.score.gateway.http.api.common.data.PageRequest;
import org.oagi.score.gateway.http.api.common.data.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class CodeListController {

    @Autowired
    private CodeListService service;

    @RequestMapping(value = "/code_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<CodeListForList> getCodeLists(
            @AuthenticationPrincipal AuthenticatedPrincipal user,
            @RequestParam(name = "releaseId") long releaseId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "states", required = false) String states,
            @RequestParam(name = "deprecated", required = false) String deprecated,
            @RequestParam(name = "extensible", required = false) Boolean extensible,
            @RequestParam(name = "ownerLoginIds", required = false) String ownerLoginIds,
            @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
            @RequestParam(name = "updateStart", required = false) String updateStart,
            @RequestParam(name = "updateEnd", required = false) String updateEnd,
            @RequestParam(name = "sortActive") String sortActive,
            @RequestParam(name = "sortDirection") String sortDirection,
            @RequestParam(name = "pageIndex") int pageIndex,
            @RequestParam(name = "pageSize") int pageSize) {

        CodeListForListRequest request = new CodeListForListRequest();

        request.setReleaseId(releaseId);
        request.setName(name);
        if (!StringUtils.isEmpty(states)) {
            List<String> stateStrings = Arrays.asList(states.split(",")).stream().collect(Collectors.toList());
            request.setStates(stateStrings.stream()
                    .map(e -> CcState.valueOf(e.trim())).collect(Collectors.toList()));
        } else {
            request.setStates(Stream.of(CcState.values()).filter(e -> e != CcState.Deleted).collect(Collectors.toList()));
        }
        if (!StringUtils.isEmpty(deprecated)) {
            if ("true".equalsIgnoreCase(deprecated.toLowerCase())) {
                request.setDeprecated(true);
            } else if ("false".equalsIgnoreCase(deprecated.toLowerCase())) {
                request.setDeprecated(false);
            }
        }
        request.setExtensible(extensible);

        request.setOwnerLoginIds(StringUtils.isEmpty(ownerLoginIds) ? Collections.emptyList() :
                Arrays.asList(ownerLoginIds.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).collect(Collectors.toList()));
        request.setUpdaterLoginIds(StringUtils.isEmpty(updaterLoginIds) ? Collections.emptyList() :
                Arrays.asList(updaterLoginIds.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).collect(Collectors.toList()));

        if (!StringUtils.isEmpty(updateStart)) {
            request.setUpdateStartDate(new Date(Long.valueOf(updateStart)));
        }
        if (!StringUtils.isEmpty(updateEnd)) {
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

        return service.getCodeLists(user, request);
    }

    @RequestMapping(value = "/code_list/{manifestId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CodeList getCodeList(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                @PathVariable("manifestId") BigInteger manifestId) {
        return service.getCodeList(user, manifestId);
    }

    @RequestMapping(value = "/code_list", method = RequestMethod.PUT)
    public CcCreateResponse create(
            @AuthenticationPrincipal AuthenticatedPrincipal user,
            @RequestBody CodeList codeList) {
        BigInteger manifestId = service.createCodeList(user, codeList);

        CcCreateResponse resp = new CcCreateResponse();
        resp.setManifestId(manifestId);
        return resp;
    }

    @RequestMapping(value = "/code_list/{manifestId}", method = RequestMethod.POST)
    public ResponseEntity update(
            @PathVariable("manifestId") BigInteger manifestId,
            @AuthenticationPrincipal AuthenticatedPrincipal user,
            @RequestBody CodeList codeList) {
        codeList.setCodeListManifestId(manifestId);
        service.update(user, codeList);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/code_list/{manifestId}/revision", method = RequestMethod.POST)
    public ResponseEntity makeNewRevision(
            @PathVariable("manifestId") BigInteger manifestId,
            @AuthenticationPrincipal AuthenticatedPrincipal user) {

        service.makeNewRevision(user, manifestId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/code_list/{manifestId}/revision", method = RequestMethod.GET)
    public CodeList getCodeListRevision(
            @PathVariable("manifestId") BigInteger manifestId,
            @AuthenticationPrincipal AuthenticatedPrincipal user) {
        return service.getCodeListRevision(user, manifestId);
    }

    @RequestMapping(value = "/code_list/{manifestId}/revision/cancel", method = RequestMethod.POST)
    public ResponseEntity cancelRevision(
            @PathVariable("manifestId") BigInteger manifestId,
            @AuthenticationPrincipal AuthenticatedPrincipal user) {

        service.cancelRevision(user, manifestId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/code_list/delete", method = RequestMethod.POST)
    public ResponseEntity deleteCodeList(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                  @RequestBody DeleteCodeListRequest request) {
        service.deleteCodeList(user, request.getCodeListManifestIds());
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/code_list/restore", method = RequestMethod.POST)
    public ResponseEntity restoreCodeList(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                  @RequestBody DeleteCodeListRequest request) {
        service.restoreCodeList(user, request.getCodeListManifestIds());
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/code_list/check_uniqueness", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean checkUniqueness(
            @RequestParam(name = "releaseId") long releaseId,
            @RequestParam(name = "codeListManifestId", required = false) Long codeListManifestId,
            @RequestParam(name = "listId") String listId,
            @RequestParam(name = "agencyId") Long agencyId,
            @RequestParam(name = "versionId") String versionId) {

        SameCodeListParams params = new SameCodeListParams();
        params.setReleaseId(releaseId);
        params.setCodeListManifestId(codeListManifestId);
        params.setListId(listId);
        params.setAgencyId(agencyId);
        params.setVersionId(versionId);

        return service.hasSameCodeList(params);
    }

    @RequestMapping(value = "/code_list/check_name_uniqueness", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean checkNameUniqueness(
            @RequestParam(name = "releaseId") long releaseId,
            @RequestParam(name = "codeListManifestId", required = false) Long codeListManifestId,
            @RequestParam(name = "codeListName") String codeListName) {

        SameNameCodeListParams params = new SameNameCodeListParams();
        params.setReleaseId(releaseId);
        params.setCodeListManifestId(codeListManifestId);
        params.setCodeListName(codeListName);

        return service.hasSameNameCodeList(params);
    }
}
