package org.oagi.srt.gateway.http.api.cc_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.cc_management.service.CcNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @RequestMapping(value = "/core_component/node/{type}/{releaseId:[\\d]+}/{id:[\\d]+}",
            method = RequestMethod.GET,
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
            case "extension":
                return getExtensionNode(user, ccId, releaseId);
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

    private CcAccNode getExtensionNode(User user, long extensionId, Long releaseId) {
        return service.getExtensionNode(user, extensionId, releaseId);
    }

    @RequestMapping(value = "/core_component/acc2",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public int getAccMaxId() {
        return service.getAccMaxId();
    }

    @RequestMapping(value = "/core_component/acc/{id}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void update(
            @AuthenticationPrincipal User user,
            @PathVariable("id") long accId,
            @RequestBody CcAccNode ccAccNode) {
        accId = ccAccNode.getAccId();
        System.out.println("===================================");
        System.out.println("acc node = " + ccAccNode);
        System.out.println("===================================");

        service.updateAcc(user, ccAccNode, accId);
    }

    @RequestMapping(value = "/core_component/acc", method = RequestMethod.PUT)
    public ResponseEntity create(
            @AuthenticationPrincipal User user,
            @RequestBody CcAccNode ccAccNode) {
        service.createAcc(user, ccAccNode);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/core_component/node/children/{type}/{releaseId:[\\d]+}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<? extends CcNode> getNodeChildren(@AuthenticationPrincipal User user,
                                                  @PathVariable("type") String type,
                                                  @PathVariable("releaseId") long releaseId,
                                                  @RequestParam("data") String data) {
        switch (type) {
            case "acc":
                CcAccNode accNode = convertValue(data, CcAccNode.class);
                accNode.setReleaseId((releaseId == 0L) ? null : releaseId);
                return service.getDescendants(user, accNode);
            case "asccp":
                CcAsccpNode asccpNode = convertValue(data, CcAsccpNode.class);
                asccpNode.setReleaseId((releaseId == 0L) ? null : releaseId);
                return service.getDescendants(user, asccpNode);
            case "bccp":
                CcBccpNode bccpNode = convertValue(data, CcBccpNode.class);
                bccpNode.setReleaseId((releaseId == 0L) ? null : releaseId);
                return service.getDescendants(user, bccpNode);
            default:
                throw new UnsupportedOperationException();
        }
    }

    private <T> T convertValue(String data, Class<T> clazz) {
        Map<String, Object> params = new HashMap();
        Arrays.stream(new String(Base64.getDecoder().decode(data)).split("&")).forEach(e -> {
            String[] keyValue = e.split("=");
            params.put(keyValue[0], keyValue[1]);
        });
        return objectMapper.convertValue(params, clazz);
    }

    @RequestMapping(value = "/core_component/node/detail/{type}/{releaseId:[\\d]+}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CcNodeDetail getNodeDetail(@AuthenticationPrincipal User user,
                                      @PathVariable("type") String type,
                                      @PathVariable("releaseId") long releaseId,
                                      @RequestParam("data") String data) {
        switch (type) {
            case "acc":
                CcAccNode accNode = convertValue(data, CcAccNode.class);
                accNode.setReleaseId((releaseId == 0L) ? null : releaseId);
                return service.getAccNodeDetail(user, accNode);
            case "asccp":
                CcAsccpNode asccpNode = convertValue(data, CcAsccpNode.class);
                asccpNode.setReleaseId((releaseId == 0L) ? null : releaseId);
                return service.getAsccpNodeDetail(user, asccpNode);
            case "bccp":
                CcBccpNode bccpNode = convertValue(data, CcBccpNode.class);
                bccpNode.setReleaseId((releaseId == 0L) ? null : releaseId);
                return service.getBccpNodeDetail(user, bccpNode);
            case "bdt_sc":
                CcBdtScNode bdtScNode = convertValue(data, CcBdtScNode.class);
                bdtScNode.setReleaseId((releaseId == 0L) ? null : releaseId);
                return service.getBdtScNodeDetail(user, bdtScNode);
            default:
                throw new UnsupportedOperationException();
        }
    }
}
