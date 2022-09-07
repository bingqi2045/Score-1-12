package org.oagi.score.gateway.http.api.cc_management.controller;

import org.oagi.score.gateway.http.api.cc_management.data.CcActionRequest;
import org.oagi.score.gateway.http.api.cc_management.data.ExtensionUpdateRequest;
import org.oagi.score.gateway.http.api.cc_management.data.ExtensionUpdateResponse;
import org.oagi.score.gateway.http.api.cc_management.data.node.CcNode;
import org.oagi.score.gateway.http.api.cc_management.service.ExtensionService;
import org.oagi.score.service.common.data.CcState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ExtensionController {

    @Autowired
    private ExtensionService service;

    @RequestMapping(value = "/core_component/node/extension/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcNode getCcNode(@AuthenticationPrincipal AuthenticatedPrincipal user,
                            @PathVariable("manifestId") String manifestId) {
        return service.getExtensionNode(user, manifestId);
    }

    @RequestMapping(value = "/core_component/extension/{manifestId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity doExtensionAction(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                            @PathVariable("manifestId") String manifestId,
                                            @RequestBody CcActionRequest actionRequest) {

        switch (actionRequest.getAction()) {
            case "append":
                switch (actionRequest.getType()) {
                    case "asccp":
                        service.appendAsccp(user, manifestId, actionRequest.getManifestId());
                        break;

                    case "bccp":
                        service.appendBccp(user, manifestId, actionRequest.getManifestId(), actionRequest.isAttribute());
                        break;
                }

                break;

            case "discard":
                switch (actionRequest.getType()) {
                    case "ascc":
                        break;

                    case "bcc":
                        break;
                }

                break;
        }

        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "/core_component/extension/{manifestId}/state",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateExtensionState(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                               @PathVariable("manifestId") String manifestId,
                                               @RequestBody Map<String, Object> body) {
        String strState = (String) body.get("state");
        if ("Purge".equals(strState)) {
            service.purgeExtension(user, manifestId);
        } else {
            CcState state = CcState.valueOf((String) body.get("state"));
            service.updateState(user, manifestId, state);
        }

        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "/core_component/extension/{manifestId}/detail",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ExtensionUpdateResponse updateDetails(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                                 @PathVariable("manifestId") String manifestId,
                                                 @RequestBody ExtensionUpdateRequest request) {
        request.setManifestId(manifestId);
        return service.updateDetails(user, request);
    }

    @RequestMapping(value = "/core_component/extension/{type}/{manifestId}/revision",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcNode getLastCcNode(@AuthenticationPrincipal AuthenticatedPrincipal user,
                                @PathVariable("type") String type,
                                @PathVariable("manifestId") String manifestId) {
        return service.getLastRevisionCc(user, type, manifestId);
    }
}
