package org.oagi.srt.gateway.http.api.cc_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oagi.srt.gateway.http.api.cc_management.data.*;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.cc_management.service.CcNodeService;
import org.oagi.srt.gateway.http.api.common.data.AccessPrivilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CcNodeController {

    @Autowired
    private CcNodeService service;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/core_component/node/{type}/{manifestId:[\\d]+}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcNode getCcNode(@AuthenticationPrincipal User user,
                            @PathVariable("type") String type,
                            @PathVariable("manifestId") BigInteger manifestId) {
        switch (type) {
            case "acc":
                return getAccNode(user, manifestId);
            case "asccp":
                return getAsccpNode(user, manifestId);
            case "bccp":
                return getBccpNode(user, manifestId);
            default:
                throw new UnsupportedOperationException();
        }
    }

    @RequestMapping(value = "/core_component",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcUpdateResponse updateCcNodeDetails(@AuthenticationPrincipal User user,
                                                @RequestBody CcUpdateRequest ccUpdateRequest) {
        return service.updateCcDetails(user, ccUpdateRequest);
    }

    @RequestMapping(value = "/core_component/node/{type}/{manifestId:[\\d]+}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcNodeUpdateResponse updateCcNodeManifest(@AuthenticationPrincipal User user,
                                                     @PathVariable("type") String type,
                                                     @PathVariable("manifestId") BigInteger manifestId,
                                                     @RequestBody CcUpdateManifestRequest ccUpdateManifestRequest) {

        CcNodeUpdateResponse resp = new CcNodeUpdateResponse();
        resp.setType(type);

        switch (type) {
            case "asccp":
                resp.setManifestId(
                        service.updateAsccpRoleOfAcc(user, manifestId, ccUpdateManifestRequest.getAccManifestId())
                );
                break;

            case "bccp":
                resp.setManifestId(
                        service.updateBccpBdt(user, manifestId, ccUpdateManifestRequest.getBdtManifestId())
                );
                break;

            default:
                throw new UnsupportedOperationException();
        }

        resp.setState(CcState.WIP.name());
        resp.setAccess(AccessPrivilege.CanEdit.name());

        return resp;
    }

    @RequestMapping(value = "/core_component/node/{type}/{manifestId:[\\d]+}/state",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcNodeUpdateResponse updateState(@AuthenticationPrincipal User user,
                                            @PathVariable("type") String type,
                                            @PathVariable("manifestId") BigInteger manifestId,
                                            @RequestBody CcUpdateStateRequest ccUpdateStateRequest) {

        CcNodeUpdateResponse resp = new CcNodeUpdateResponse();
        resp.setType(type);

        switch (type) {
            case "acc":
                resp.setManifestId(
                        service.updateAccState(user, manifestId, ccUpdateStateRequest.getState())
                );
                break;
            case "asccp":
                resp.setManifestId(
                        service.updateAsccpState(user, manifestId, ccUpdateStateRequest.getState())
                );
                break;
            case "bccp":
                resp.setManifestId(
                        service.updateBccpState(user, manifestId, ccUpdateStateRequest.getState())
                );
                break;
            default:
                throw new UnsupportedOperationException();
        }

        resp.setState(ccUpdateStateRequest.getState());
        resp.setAccess(
                ((CcState.WIP == CcState.valueOf(resp.getState())) ? AccessPrivilege.CanEdit : AccessPrivilege.CanMove).name()
        );

        return resp;
    }

    @RequestMapping(value = "/core_component/node/{type}/{manifestId:[\\d]+}/revision",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcNodeUpdateResponse makeNewRevision(@AuthenticationPrincipal User user,
                                                @PathVariable("type") String type,
                                                @PathVariable("manifestId") BigInteger manifestId) {

        CcNodeUpdateResponse resp = new CcNodeUpdateResponse();
        resp.setType(type);

        switch (type) {
            case "acc":
                resp.setManifestId(
                        service.makeNewRevisionForAcc(user, manifestId)
                );
                break;
            case "asccp":
                resp.setManifestId(
                        service.makeNewRevisionForAsccp(user, manifestId)
                );
                break;
            case "bccp":
                resp.setManifestId(
                        service.makeNewRevisionForBccp(user, manifestId)
                );
                break;
            default:
                throw new UnsupportedOperationException();
        }

        resp.setState(CcState.WIP.name());
        resp.setAccess(AccessPrivilege.CanEdit.name());

        return resp;
    }

    private CcAccNode getAccNode(User user, BigInteger manifestId) {
        return service.getAccNode(user, manifestId);
    }

    private CcAsccpNode getAsccpNode(User user, BigInteger manifestId) {
        return service.getAsccpNode(user, manifestId);
    }

    private CcBccpNode getBccpNode(User user, BigInteger manifestId) {
        return service.getBccpNode(user, manifestId);
    }

    @RequestMapping(value = "/core_component/node/{type}/{manifestId:[\\d]+}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteCcNode(@AuthenticationPrincipal User user,
                                       @PathVariable("type") String type,
                                       @PathVariable("manifestId") BigInteger manifestId) {
        switch (type) {
            case "acc":
                service.deleteAcc(user, manifestId);
                break;
            case "asccp":
                service.deleteAsccp(user, manifestId);
                break;
            case "bccp":
                service.deleteBccp(user, manifestId);
                break;
            case "ascc":
                service.deleteAscc(user, manifestId);
                break;
            case "bcc":
                service.deleteBcc(user, manifestId);
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "/core_component/asccp/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcAsccpNodeDetail.Asccp getAsccp(@PathVariable("id") long id) {
        return service.getAsccp(id);
    }

    private <T> T convertValue(String data, Class<T> clazz) {
        Map<String, Object> params = new HashMap();
        Arrays.stream(new String(Base64.getDecoder().decode(data)).split("&")).forEach(e -> {
            String[] keyValue = e.split("=");
            Object value = keyValue[1];
            if (!"null".equals(value) && value != null) {
                params.put(keyValue[0], value);
            }
        });
        return objectMapper.convertValue(params, clazz);
    }

    @RequestMapping(value = "/core_component/node/detail/{type}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcNodeDetail getNodeDetail(@AuthenticationPrincipal User user,
                                      @PathVariable("type") String type,
                                      @RequestParam("data") String data) {
        switch (type) {
            case "acc":
                CcAccNode accNode = convertValue(data, CcAccNode.class);
                return service.getAccNodeDetail(user, accNode);
            case "asccp":
                CcAsccpNode asccpNode = convertValue(data, CcAsccpNode.class);
                return service.getAsccpNodeDetail(user, asccpNode);
            case "bccp":
                CcBccpNode bccpNode = convertValue(data, CcBccpNode.class);
                return service.getBccpNodeDetail(user, bccpNode);
            case "bdt_sc":
                CcBdtScNode bdtScNode = convertValue(data, CcBdtScNode.class);
                return service.getBdtScNodeDetail(user, bdtScNode);
            default:
                throw new UnsupportedOperationException();
        }
    }

    @RequestMapping(value = "/core_component/node/append",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcCreateResponse appendNode(@AuthenticationPrincipal User user,
                                       @RequestBody CcAppendRequest ccAppendRequest) {

        long manifestId = 0L;
        if (ccAppendRequest.getAccManifestId() != null) {
            if (ccAppendRequest.getAsccpManifestId() != null) {
                manifestId = service.appendAsccp(user,
                        ccAppendRequest.getReleaseId(),
                        ccAppendRequest.getAccManifestId(),
                        ccAppendRequest.getAsccpManifestId());
            }
            if (ccAppendRequest.getBccpManifestId() != null) {
                manifestId = service.appendBccp(user,
                        ccAppendRequest.getReleaseId(),
                        ccAppendRequest.getAccManifestId(),
                        ccAppendRequest.getBccpManifestId());
            }
        }
        CcCreateResponse response = new CcCreateResponse();
        response.setManifestId(BigInteger.valueOf(manifestId));
        return response;
    }

    @RequestMapping(value = "/core_component/node/acc/{manifestId}/base",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcNodeUpdateResponse setBasedNode(@AuthenticationPrincipal User user,
                                             @PathVariable("manifestId") BigInteger manifestId,
                                             @RequestBody CcAccRequest ccAccRequest) {
        CcNodeUpdateResponse resp = new CcNodeUpdateResponse();
        resp.setType("acc");
        resp.setManifestId(
                service.updateAccBasedAcc(user, manifestId, ccAccRequest.getBasedAccManifestId())
        );
        resp.setState(CcState.WIP.name());
        resp.setAccess(AccessPrivilege.CanEdit.name());

        return resp;
    }

    @RequestMapping(value = "/core_component/acc", method = RequestMethod.POST)
    public CcCreateResponse createAcc(@AuthenticationPrincipal User user,
                                      @RequestBody CcAccCreateRequest request) {
        BigInteger manifestId = service.createAcc(user, request);

        CcCreateResponse resp = new CcCreateResponse();
        resp.setManifestId(manifestId);
        return resp;
    }

    @RequestMapping(value = "/core_component/asccp", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcCreateResponse createAsccp(@AuthenticationPrincipal User user,
                                        @RequestBody CcAsccpCreateRequest request) {
        BigInteger manifestId = service.createAsccp(user, request);

        CcCreateResponse resp = new CcCreateResponse();
        resp.setManifestId(manifestId);
        return resp;
    }

    @RequestMapping(value = "/core_component/bccp", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcCreateResponse createBccp(@AuthenticationPrincipal User user,
                                       @RequestBody CcBccpCreateRequest request) {
        BigInteger manifestId = service.createBccp(user, request);

        CcCreateResponse resp = new CcCreateResponse();
        resp.setManifestId(manifestId);
        return resp;
    }

    @RequestMapping(value = "/core_component/node/{type}/{manifestId}/revision", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcRevisionResponse getCcNodeRevision(@AuthenticationPrincipal User user,
                                                @PathVariable("type") String type,
                                                @PathVariable("manifestId") BigInteger manifestId) {
        switch (type) {
            case "acc":
                return service.getAccNodeRevision(user, manifestId);
            case "asccp":
                return service.getAsccpNodeRevision(user, manifestId);
            case "bccp":
                return service.getBccpNodeRevision(user, manifestId);
            default:
                throw new UnsupportedOperationException();
        }
    }
}