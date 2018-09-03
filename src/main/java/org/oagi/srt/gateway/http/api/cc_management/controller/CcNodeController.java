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

    @RequestMapping(value = "/core_component/node/{type}/{releaseId:[\\d]+}/{id:[\\d]+}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CcNode getCcNode(@AuthenticationPrincipal User user,
                            @PathVariable("type") String type,
                            @PathVariable("releaseId") long releaseId,
                            @PathVariable("id") long ccId) {
        switch (type) {
            case "acc":
                return getAccNode(user, ccId, (releaseId == 0L) ? null : releaseId);
            case "asccp":
                return getAsccpNode(user, ccId, (releaseId == 0L) ? null : releaseId);
            case "bccp":
                return getBccpNode(user, ccId, (releaseId == 0L) ? null : releaseId);
            default:
                throw new UnsupportedOperationException();
        }
    }

    private CcAccNode getAccNode(User user, long accId, Long releaseId) {
        return service.getAccNode(user, accId, releaseId);
    }

    private CcAsccpNode getAsccpNode(User user, long asccpId, Long releaseId) {
        return service.getAsccpNode(user, asccpId, releaseId);
    }

    private CcBccpNode getBccpNode(User user, long bccpId, Long releaseId) {
        return service.getBccpNode(user, bccpId, releaseId);
    }

    @RequestMapping(value = "/core_component/node/children/acc/{releaseId:[\\d]+}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<? extends CcNode> getAccNodeChildren(@AuthenticationPrincipal User user,
                                                     @PathVariable("releaseId") long releaseId,
                                                     @RequestParam("data") String data) {
        CcAccNode accNode = convertValue(data, CcAccNode.class);
        accNode.setReleaseId((releaseId == 0L) ? null : releaseId);
        return service.getDescendants(user, accNode);
    }

    @RequestMapping(value = "/core_component/node/children/asccp/{releaseId:[\\d]+}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<? extends CcNode> getAsccpNodeChildren(@AuthenticationPrincipal User user,
                                                       @PathVariable("releaseId") long releaseId,
                                                       @RequestParam("data") String data) {
        CcAsccpNode asccpNode = convertValue(data, CcAsccpNode.class);
        asccpNode.setReleaseId((releaseId == 0L) ? null : releaseId);
        return service.getDescendants(user, asccpNode);
    }

    @RequestMapping(value = "/core_component/node/children/bccp/{releaseId:[\\d]+}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<? extends CcNode> getBccpNodeChildren(@AuthenticationPrincipal User user,
                                                      @PathVariable("releaseId") long releaseId,
                                                      @RequestParam("data") String data) {
        CcBccpNode bccpNode = convertValue(data, CcBccpNode.class);
        bccpNode.setReleaseId((releaseId == 0L) ? null : releaseId);
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
