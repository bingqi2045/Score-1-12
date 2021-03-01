package org.oagi.score.gateway.http.api.module_management.controller;

import org.oagi.score.export.ExportContext;
import org.oagi.score.export.ExportContextBuilder;
import org.oagi.score.export.impl.DefaultExportContextBuilder;
import org.oagi.score.export.impl.XMLExportSchemaModuleVisitor;
import org.oagi.score.export.model.SchemaModule;
import org.oagi.score.gateway.http.api.module_management.service.ModuleSetReleaseService;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.repo.api.module.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.oagi.score.repo.api.base.SortDirection.ASC;
import static org.oagi.score.repo.api.base.SortDirection.DESC;

@RestController
public class ModuleSetReleaseController {

    @Autowired
    private ModuleSetReleaseService service;

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/module_set_release_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GetModuleSetReleaseListResponse getModuleSetReleaseList(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                                                   @RequestParam(name = "name", required = false) String name,
                                                                   @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
                                                                   @RequestParam(name = "updateStart", required = false) String updateStart,
                                                                   @RequestParam(name = "updateEnd", required = false) String updateEnd,
                                                                   @RequestParam(name = "sortActive") String sortActive,
                                                                   @RequestParam(name = "sortDirection") String sortDirection,
                                                                   @RequestParam(name = "pageIndex") int pageIndex,
                                                                   @RequestParam(name = "pageSize") int pageSize) {

        GetModuleSetReleaseListRequest request = new GetModuleSetReleaseListRequest(sessionService.asScoreUser(user));

        request.setName(name);
        request.setUpdaterUsernameList(!StringUtils.hasLength(updaterLoginIds) ? Collections.emptyList() :
                Arrays.asList(updaterLoginIds.split(","))
                        .stream().map(e -> e.trim()).filter(e -> StringUtils.hasLength(e))
                        .collect(Collectors.toList()));

        if (StringUtils.hasLength(updateStart)) {
            request.setUpdateStartDate(new Timestamp(Long.valueOf(updateStart)).toLocalDateTime());
        }
        if (StringUtils.hasLength(updateEnd)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(updateEnd));
            calendar.add(Calendar.DATE, 1);
            request.setUpdateEndDate(new Timestamp(calendar.getTimeInMillis()).toLocalDateTime());
        }

        request.setPageIndex(pageIndex);
        request.setPageSize(pageSize);
        request.setSortActive(sortActive);
        request.setSortDirection("asc".equalsIgnoreCase(sortDirection) ? ASC : DESC);

        return service.getModuleSetReleaseList(request);
    }

    @RequestMapping(value = "/module_set_release/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ModuleSetRelease getModuleSetRelease(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                                @PathVariable("id") BigInteger moduleSetReleaseId) {
        GetModuleSetReleaseRequest request = new GetModuleSetReleaseRequest(sessionService.asScoreUser(user));
        request.setModuleSetReleaseId(moduleSetReleaseId);
        GetModuleSetReleaseResponse response = service.getModuleSetRelease(request);
        return response.getModuleSetRelease();
    }

    @RequestMapping(value = "/module_set_release", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ModuleSetRelease createModuleSetRelease(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                                   @RequestBody CreateModuleSetReleaseRequest request) {
        request.setRequester(sessionService.asScoreUser(user));
        CreateModuleSetReleaseResponse response = service.createModuleSetRelease(request);
        return response.getModuleSetRelease();
    }

    @RequestMapping(value = "/module_set_release/{id}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ModuleSetRelease updateModuleSetRelease(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                     @PathVariable("id") BigInteger moduleSetReleaseId,
                                     @RequestBody ModuleSetRelease moduleSetRelease) {
        UpdateModuleSetReleaseRequest request = new UpdateModuleSetReleaseRequest(sessionService.asScoreUser(user));
        request.setModuleSetReleaseId(moduleSetReleaseId);
        request.setModuleSetId(moduleSetRelease.getModuleSetId());
        request.setReleaseId(moduleSetRelease.getReleaseId());
        request.setDefault(moduleSetRelease.isDefault());
        UpdateModuleSetReleaseResponse response = service.updateModuleSetRelease(request);
        return response.getModuleSetRelease();
    }

    @RequestMapping(value = "/module_set_release/{id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void discardModuleSetRelease(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                 @PathVariable("id") BigInteger moduleSetReleaseId) {
        DeleteModuleSetReleaseRequest request = new DeleteModuleSetReleaseRequest(sessionService.asScoreUser(user));
        request.setModuleSetReleaseId(moduleSetReleaseId);
        service.discardModuleSetRelease(request);
    }

    @RequestMapping(value = "/module_set_release/{id}/export", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> exportModuleSetRelease(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                                                        @PathVariable("id") BigInteger moduleSetReleaseId) throws Exception {

        File output = service.exportModuleSetRelease(moduleSetReleaseId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.zip")
                .contentType(MediaType.parseMediaType("application/zip"))
                .contentLength(output.length())
                .body(new InputStreamResource(new FileInputStream(output)));
    }
}
