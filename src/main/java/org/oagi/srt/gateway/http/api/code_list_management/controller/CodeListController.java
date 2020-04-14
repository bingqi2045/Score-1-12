package org.oagi.srt.gateway.http.api.code_list_management.controller;

import org.oagi.srt.gateway.http.api.code_list_management.data.*;
import org.oagi.srt.gateway.http.api.code_list_management.service.CodeListService;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
public class CodeListController {

    @Autowired
    private CodeListService service;

    @RequestMapping(value = "/code_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<CodeListForList> getCodeLists(
            @RequestParam(name = "releaseId") long releaseId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "states", required = false) String states,
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
        request.setStates(StringUtils.isEmpty(states) ? Collections.emptyList() :
                Arrays.asList(states.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).collect(Collectors.toList()));
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

        return service.getCodeLists(request);
    }

    @RequestMapping(value = "/code_list/{manifestId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CodeList getCodeList(@PathVariable("manifestId") long manifestId) {
        return service.getCodeList(manifestId);
    }

    @RequestMapping(value = "/code_list", method = RequestMethod.PUT)
    public ResponseEntity create(
            @AuthenticationPrincipal User user,
            @RequestBody CodeList codeList) {
        service.insert(user, codeList);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/code_list/{manifestId}", method = RequestMethod.POST)
    public ResponseEntity update(
            @PathVariable("manifestId") long manifestId,
            @AuthenticationPrincipal User user,
            @RequestBody CodeList codeList) {
        codeList.setCodeListManifestId(manifestId);
        service.update(user, codeList);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/code_list/{manifestId}", method = RequestMethod.DELETE)
    public ResponseEntity delete(
            @PathVariable("manifestId") long manifestId) {
        service.delete(manifestId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/code_list/delete", method = RequestMethod.POST)
    public ResponseEntity deletes(@RequestBody DeleteCodeListRequest request) {
        service.delete(request.getCodeListIds());
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
