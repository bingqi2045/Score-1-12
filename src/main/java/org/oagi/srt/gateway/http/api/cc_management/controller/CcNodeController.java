package org.oagi.srt.gateway.http.api.cc_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcAccNode;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcAsccpNode;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcBccpNode;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcNode;
import org.oagi.srt.gateway.http.api.cc_management.service.CcNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CcNodeController {

    @Autowired
    private CcNodeService service;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/core_component/node/{type}/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CcNode getCcNode(@AuthenticationPrincipal User user,
                            @PathVariable("type") String type,
                            @PathVariable("id") long ccId,
                            @RequestParam(value = "releaseId", required = false) long releaseId) {
        switch (type) {
            case "acc":
                return getAccNode(user, ccId, releaseId);
            case "asccp":
                return getAsccpNode(user, ccId, releaseId);
            case "bccp":
                return getBccpNode(user, ccId, releaseId);
            default:
                throw new UnsupportedOperationException();
        }
    }

    private CcAccNode getAccNode(User user, long accId, long releaseId) {
        return service.getAccNode(user, accId, releaseId);
    }

    private CcAsccpNode getAsccpNode(User user, long asccpId, long releaseId) {
        return service.getAsccpNode(user, asccpId, releaseId);
    }

    private CcBccpNode getBccpNode(User user, long bccpId, long releaseId) {
        return service.getBccpNode(user, bccpId, releaseId);
    }

    @RequestMapping(value = "/core_component/node/children/acc", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<CcNode> getAccNodeChildren(@AuthenticationPrincipal User user,
                                           @RequestParam("data") String data) {
        CcAccNode accNode = convertValue(data, CcAccNode.class);
        return service.getDescendants(user, accNode);
    }

    @RequestMapping(value = "/core_component/node/children/asccp", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<CcNode> getAsccpNodeChildren(@AuthenticationPrincipal User user,
                                             @RequestParam("data") String data) {
        CcAsccpNode asccpNode = convertValue(data, CcAsccpNode.class);
        return service.getDescendants(user, asccpNode);
    }

    @RequestMapping(value = "/core_component/node/children/bccp", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<CcNode> getBccpNodeChildren(@AuthenticationPrincipal User user,
                                            @RequestParam("data") String data) {
        CcBccpNode bccpNode = convertValue(data, CcBccpNode.class);
        return service.getDescendants(user, bccpNode);
    }

    private <T> T convertValue(String data, Class<T> clazz) {
        Map<String, Object> params = new HashMap();
        Arrays.stream(new String(Base64.getDecoder().decode(data)).split("&")).forEach(e -> {
            String[] keyValue = e.split("=");
            params.put(keyValue[0], keyValue[1]);
        });
        return objectMapper.convertValue(params, clazz);
    }
}
