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

import java.util.*;

@RestController
public class BusinessContextController {

    @Autowired
    private BusinessContextService service;

    @RequestMapping(value = "/business_contexts", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public PageResponse<BusinessContext> getBusinessContextList(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
            @RequestParam(name = "updateStart", required = false) String updateStart,
            @RequestParam(name = "updateEnd", required = false) String updateEnd,
            @RequestParam(name = "sortActive") String sortActive,
            @RequestParam(name = "sortDirection") String sortDirection,
            @RequestParam(name = "pageIndex") int pageIndex,
            @RequestParam(name = "pageSize") int pageSize) {

        BusinessContextListRequest request = new BusinessContextListRequest();

        request.setName(name);

        request.setUpdaterLoginIds(StringUtils.isEmpty(updaterLoginIds) ?
                Collections.emptyList() : Arrays.asList(updaterLoginIds.split(",")));

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
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BusinessContext getBusinessContext(@PathVariable("id") long id) {
        return service.getBusinessContext(id);
    }

    @RequestMapping(value = "/business_context_values", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BusinessContextValue> getBusinessContextValues() {
        return service.getBusinessContextValues();
    }

    @RequestMapping(value = "/simple_business_contexts", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<SimpleBusinessContext> getSimpleBusinessContextList() {
        return service.getSimpleBusinessContextList();
    }

    @RequestMapping(value = "/simple_business_context/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public SimpleBusinessContext getSimpleBusinessContext(@PathVariable("id") long id) {
        return service.getSimpleBusinessContext(id);
    }

    @RequestMapping(value = "/context_scheme/{id}/simple_context_scheme_values", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<SimpleContextSchemeValue> getSimpleContextSchemeValueList(@PathVariable("id") long ctxSchemeId) {
        return service.getSimpleContextSchemeValueList(ctxSchemeId);
    }

    @RequestMapping(value = "/business_context_values_from_biz_ctx/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BusinessContextValue> getBusinessCtxValuesFromBizCtx(@PathVariable("id") long businessCtxID) {
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
            @PathVariable("id") long id,
            @AuthenticationPrincipal User user,
            @RequestBody BusinessContext businessContext) {
        businessContext.setBizCtxId(id);
        service.update(user, businessContext);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/business_context/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(
            @PathVariable("id") long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/business_context/delete", method = RequestMethod.POST)
    public ResponseEntity deletes(@RequestBody DeleteBusinessContextRequest request) {
        service.delete(request.getBizCtxIds());
        return ResponseEntity.noContent().build();
    }
}
