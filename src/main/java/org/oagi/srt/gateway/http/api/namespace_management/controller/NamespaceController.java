package org.oagi.srt.gateway.http.api.namespace_management.controller;

import com.google.common.collect.ImmutableMap;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.namespace_management.data.Namespace;
import org.oagi.srt.gateway.http.api.namespace_management.data.NamespaceList;
import org.oagi.srt.gateway.http.api.namespace_management.data.NamespaceListRequest;
import org.oagi.srt.gateway.http.api.namespace_management.data.SimpleNamespace;
import org.oagi.srt.gateway.http.api.namespace_management.service.NamespaceService;
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
public class NamespaceController {

    @Autowired
    private NamespaceService service;

    @RequestMapping(value = "/simple_namespaces", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SimpleNamespace> getSimpleNamespaces() {
        return service.getSimpleNamespaces();
    }

    @RequestMapping(value = "/namespace_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<NamespaceList> getNamespaceList(@AuthenticationPrincipal User user,
                                                        @RequestParam(name = "uri", required = false) String uri,
                                                        @RequestParam(name = "prefix", required = false) String prefix,
                                                        @RequestParam(name = "description", required = false) String description,
                                                        @RequestParam(name = "ownerLoginIds", required = false) String ownerLoginIds,
                                                        @RequestParam(name = "updaterLoginIds", required = false) String updaterLoginIds,
                                                        @RequestParam(name = "updateStart", required = false) String updateStart,
                                                        @RequestParam(name = "updateEnd", required = false) String updateEnd,
                                                        @RequestParam(name = "sortActive") String sortActive,
                                                        @RequestParam(name = "sortDirection") String sortDirection,
                                                        @RequestParam(name = "pageIndex") int pageIndex,
                                                        @RequestParam(name = "pageSize") int pageSize) {
        NamespaceListRequest request = new NamespaceListRequest();

        request.setUri(uri);
        request.setPrefix(prefix);
        request.setDescription(description);
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
        return service.getNamespaceList(user, request);
    }

    @RequestMapping(value = "/namespace/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Namespace getNamespace(@AuthenticationPrincipal User user,
                                  @PathVariable("id") BigInteger namespaceId) {
        return service.getNamespace(user, namespaceId);
    }

    @RequestMapping(value = "/namespace", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> createNamespace(@AuthenticationPrincipal User user,
                                               @RequestBody Namespace namespace) {
        BigInteger namespaceId = service.create(user, namespace);
        return ImmutableMap.<String, Object>builder()
                .put("namespaceId", namespaceId)
                .build();
    }

    @RequestMapping(value = "/namespace/{id}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateNamespace(@PathVariable("id") BigInteger namespaceId,
                                          @AuthenticationPrincipal User user,
                                          @RequestBody Namespace namespace) {
        namespace.setNamespaceId(namespaceId);
        service.update(user, namespace);
        return ResponseEntity.accepted().build();
    }
}
