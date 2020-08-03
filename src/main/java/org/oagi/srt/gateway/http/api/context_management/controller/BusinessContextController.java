package org.oagi.srt.gateway.http.api.context_management.controller;

import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.context_management.data.*;
import org.oagi.srt.gateway.http.api.context_management.service.BusinessContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class BusinessContextController {

    @Autowired
    private BusinessContextService service;

    @RequestMapping(value = "/business_contexts", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<BusinessContext> getBusinessContextList(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "bizCtxIds", required = false) String bizCtxIds,
            @RequestParam(name = "topLevelAsbiepId", required = false) BigInteger topLevelAsbiepId,
            @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
            @RequestParam(name = "updateStart", required = false) String updateStart,
            @RequestParam(name = "updateEnd", required = false) String updateEnd,
            @RequestParam(name = "sortActive", required = false) String sortActive,
            @RequestParam(name = "sortDirection", required = false) String sortDirection,
            @RequestParam(name = "pageIndex", defaultValue = "-1") int pageIndex,
            @RequestParam(name = "pageSize", defaultValue = "-1") int pageSize) {

        BusinessContextListRequest request = new BusinessContextListRequest();

        request.setName(name);
        request.setTopLevelAsbiepId(topLevelAsbiepId);
        request.setBizCtxIds(StringUtils.isEmpty(bizCtxIds) ? Collections.emptyList() :
                Arrays.asList(bizCtxIds.split(",")).stream().map(e -> e.trim()).filter(e -> !StringUtils.isEmpty(e)).map(e -> Long.valueOf(e)).collect(Collectors.toList()));
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

        return service.getBusinessContextList(request);
    }

    @RequestMapping(value = "/business_context/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BusinessContext getBusinessContext(@PathVariable("id") BigInteger id) {
        return service.getBusinessContext(id);
    }

    @RequestMapping(value = "/business_context_values", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BusinessContextValue> getBusinessContextValues() {
        return service.getBusinessContextValues();
    }

    @RequestMapping(value = "/context_scheme/{id}/simple_context_scheme_values", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SimpleContextSchemeValue> getSimpleContextSchemeValueList(@PathVariable("id") BigInteger ctxSchemeId) {
        return service.getSimpleContextSchemeValueList(ctxSchemeId);
    }

    @RequestMapping(value = "/business_context_values_from_biz_ctx/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BusinessContextValue> getBusinessCtxValuesFromBizCtx(@PathVariable("id") BigInteger businessCtxID) {
        return service.getBusinessContextValuesByBusinessCtxId(businessCtxID);
    }

    @RequestMapping(value = "/business_context", method = RequestMethod.PUT)
    public ResponseEntity create(
            @AuthenticationPrincipal User user,
            @RequestBody BusinessContext businessContext) {
        service.insert(user, businessContext);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/business_context/{id}", method = RequestMethod.POST)
    public ResponseEntity update(
            @PathVariable("id") BigInteger id,
            @AuthenticationPrincipal User user,
            @RequestBody BusinessContext businessContext) {
        businessContext.setBizCtxId(id);
        service.update(user, businessContext);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/business_context/{id}", method = RequestMethod.PUT)
    public ResponseEntity assign(
            @AuthenticationPrincipal User user,
            @PathVariable("id") BigInteger id,
            @RequestParam(name = "topLevelAsbiepId", required = true) BigInteger topLevelAsbiepId) {
        service.assign(id, topLevelAsbiepId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/business_context/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(
            @AuthenticationPrincipal User user,
            @PathVariable("id") BigInteger id,
            @RequestParam(name = "topLevelAsbiepId", required = false) BigInteger topLevelAsbiepId) {
        if (topLevelAsbiepId != null) {
            service.dismiss(id, topLevelAsbiepId);
        } else {
            service.delete(id);
        }
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/business_context/delete", method = RequestMethod.POST)
    public ResponseEntity deletes(@RequestBody DeleteBusinessContextRequest request) {
        service.delete(request.getBizCtxIds());
        return ResponseEntity.noContent().build();
    }
}
