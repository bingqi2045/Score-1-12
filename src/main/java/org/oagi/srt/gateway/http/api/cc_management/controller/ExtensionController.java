package org.oagi.srt.gateway.http.api.cc_management.controller;

import org.oagi.srt.gateway.http.api.cc_management.data.CcActionRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.data.ExtensionUpdateRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.ExtensionUpdateResponse;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcNode;
import org.oagi.srt.gateway.http.api.cc_management.service.ExtensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ExtensionController {

    @Autowired
    private ExtensionService service;

    @RequestMapping(value = "/core_component/node/extension/{manifestId:[\\d]+}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcNode getCcNode(@AuthenticationPrincipal User user,
                            @PathVariable("manifestId") long manifestId) {
        return service.getExtensionNode(user, manifestId);
    }

    @RequestMapping(value = "/core_component/extension/{manifestId:[\\d]+}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity doExtensionAction(@AuthenticationPrincipal User user,
                                            @PathVariable("manifestId") long manifestId,
                                            @RequestBody CcActionRequest actionRequest) {

        switch (actionRequest.getAction()) {
            case "append":
                switch (actionRequest.getType()) {
                    case "asccp":
                        service.appendAsccp(user, manifestId, actionRequest.getManifestId());
                        break;

                    case "bccp":
                        service.appendBccp(user, manifestId, actionRequest.getManifestId());
                        break;
                }

                break;

            case "discard":
                switch (actionRequest.getType()) {
                    case "ascc":
                        service.discardAscc(user, manifestId, actionRequest.getId());
                        break;

                    case "bcc":
                        service.discardBcc(user, manifestId, actionRequest.getId());
                        break;
                }

                break;
        }

        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "/core_component/extension/{manifestId:[\\d]+}/state",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateExtensionState(@AuthenticationPrincipal User user,
                                               @PathVariable("manifestId") long manifestId,
                                               @RequestBody Map<String, Object> body) {
        CcState state = CcState.valueOf((String) body.get("state"));
        service.updateState(user, manifestId, state);

        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "/core_component/extension/{manifestId:[\\d]+}/detail",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ExtensionUpdateResponse updateDetails(@AuthenticationPrincipal User user,
                                                 @PathVariable("manifestId") long manifestId,
                                                 @RequestBody ExtensionUpdateRequest request) {
        request.setManifestId(manifestId);
        return service.updateDetails(user, request);
    }

    @RequestMapping(value = "/core_component/extension//{type}/{manifestId:[\\d]+}/reivision",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcNode getLastCcNode(@AuthenticationPrincipal User user,
                                @PathVariable("type") String type,
                                @PathVariable("manifestId") long manifestId) {
        return service.getLastRevisionCc(user, type, manifestId);
    }
}
