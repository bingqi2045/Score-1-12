package org.oagi.srt.gateway.http.api.cc_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oagi.srt.gateway.http.api.cc_management.data.*;
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

    @RequestMapping(value = "/core_component/node/{type}/{manifestId:[\\d]+}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcNode getCcNode(@AuthenticationPrincipal User user,
                            @PathVariable("type") String type,
                            @PathVariable("manifestId") long manifestId) {
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
    public CcNode updateCcNodeManifest(@AuthenticationPrincipal User user,
                                       @PathVariable("type") String type,
                                       @PathVariable("manifestId") long manifestId,
                                       @RequestBody CcUpdateManifestRequest ccUpdateManifestRequest) {
        switch (type) {
            case "asccp":
                return service.updateAsccpRoleOfAcc(user, manifestId, ccUpdateManifestRequest.getAccManifestId());

            case "bccp":
                return service.updateBccpBdt(user, manifestId, ccUpdateManifestRequest.getBdtManifestId());

            default:
                throw new UnsupportedOperationException();
        }
    }

    @RequestMapping(value = "/core_component/node/{type}/{manifestId:[\\d]+}/state",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcNode updateState(@AuthenticationPrincipal User user,
                              @PathVariable("type") String type,
                              @PathVariable("manifestId") long manifestId,
                              @RequestBody CcUpdateStateRequest ccUpdateStateRequest) {
        switch (type) {
            case "acc":
                return service.updateAccState(user, manifestId, ccUpdateStateRequest.getState());
            case "asccp":
                return service.updateAsccpState(user, manifestId, ccUpdateStateRequest.getState());
            case "bccp":
                return service.updateBccpState(user, manifestId, ccUpdateStateRequest.getState());

            default:
                throw new UnsupportedOperationException();
        }
    }

    @RequestMapping(value = "/core_component/node/{type}/{manifestId:[\\d]+}/revision",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcNode makeNewRevision(@AuthenticationPrincipal User user,
                                  @PathVariable("type") String type,
                                  @PathVariable("manifestId") long manifestId) {
        switch (type) {
            case "acc":
                return service.makeNewRevisionForAcc(user, manifestId);
            case "asccp":
                return service.makeNewRevisionForAsccp(user, manifestId);
            case "bccp":
                return service.makeNewRevisionForBccp(user, manifestId);

            default:
                throw new UnsupportedOperationException();
        }
    }

    @RequestMapping(value = "/core_component/node/{type}/{manifestId:[\\d]+}/discard",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void discardCc(@AuthenticationPrincipal User user,
                          @PathVariable("type") String type,
                          @PathVariable("manifestId") long manifestId) {
        switch (type) {
            case "asccp":
                service.discardAscc(user, manifestId);
                break;
            case "bccp":
                service.discardBcc(user, manifestId);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private CcAccNode getAccNode(User user, long manifestId) {
        return service.getAccNode(user, manifestId);
    }

    private CcAsccpNode getAsccpNode(User user, long manifestId) {
        return service.getAsccpNode(user, manifestId);
    }

    private CcBccpNode getBccpNode(User user, long manifestId) {
        return service.getBccpNode(user, manifestId);
    }


    @RequestMapping(value = "/core_component",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteCcNodes(@AuthenticationPrincipal User user,
                                        @RequestParam(value = "acc", required = false) List<Long> accManifestIdList,
                                        @RequestParam(value = "asccp", required = false) List<Long> asccpManifestIdList,
                                        @RequestParam(value = "bccp", required = false) List<Long> bccpManifestIdList) {

        accManifestIdList.forEach(manifestId -> {
            service.updateAccState(user, manifestId.longValue(), CcState.Deleted.name());
        });

        asccpManifestIdList.forEach(manifestId -> {
            service.updateAsccpState(user, manifestId.longValue(), CcState.Deleted.name());
        });

        asccpManifestIdList.forEach(manifestId -> {
            service.updateBccpState(user, manifestId.longValue(), CcState.Deleted.name());
        });

        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "/core_component/node/{type}/{manifestId:[\\d]+}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteCcNode(@AuthenticationPrincipal User user,
                                       @PathVariable("type") String type,
                                       @PathVariable("manifestId") long manifestId) {
        switch (type) {
            case "acc":
                deleteAccNode(user, manifestId);
                break;
            case "asccp":
                deleteAsccpNode(user, manifestId);
                break;
            case "bccp":
                deleteBccpNode(user, manifestId);
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return ResponseEntity.accepted().build();
    }

    private void deleteAccNode(User user, long manifestId) {
        service.deleteAccNode(user, manifestId);
    }

    private void deleteAsccpNode(User user, long manifestId) {
        service.deleteAsccpNode(user, manifestId);
    }

    private void deleteBccpNode(User user, long manifestId) {
        service.deleteBccpNode(user, manifestId);
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
                manifestId = service.appendAsccp(user, ccAppendRequest.getAccManifestId(), ccAppendRequest.getAsccpManifestId());
            }
            if (ccAppendRequest.getBccpManifestId() != null) {
                manifestId = service.appendBccp(user, ccAppendRequest.getAccManifestId(), ccAppendRequest.getBccpManifestId());
            }
        }
        CcCreateResponse response = new CcCreateResponse();
        response.setManifestId(manifestId);
        return response;
    }

    @RequestMapping(value = "/core_component/node/acc/{manifestId}/base",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcNode setBasedNode(@AuthenticationPrincipal User user,
                               @PathVariable("manifestId") long manifestId,
                               @RequestBody CcAccRequest ccAccRequest) {
        if (ccAccRequest.getBasedAccManifestId() == null) {
            return service.discardAccBasedId(user, manifestId);
        }
        return service.updateAccBasedId(user, manifestId, ccAccRequest.getBasedAccManifestId());
    }

    @RequestMapping(value = "/core_component/acc", method = RequestMethod.POST)
    public CcCreateResponse createAcc(@AuthenticationPrincipal User user,
                                      @RequestBody CcAccCreateRequest request) {
        long manifestId = service.createAcc(user, request);

        CcCreateResponse resp = new CcCreateResponse();
        resp.setManifestId(manifestId);
        return resp;
    }

    @RequestMapping(value = "/core_component/asccp", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcCreateResponse createAsccp(@AuthenticationPrincipal User user,
                                        @RequestBody CcAsccpCreateRequest request) {
        long manifestId = service.createAsccp(user, request);

        CcCreateResponse resp = new CcCreateResponse();
        resp.setManifestId(manifestId);
        return resp;
    }

    @RequestMapping(value = "/core_component/bccp", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcCreateResponse createBccp(@AuthenticationPrincipal User user,
                                       @RequestBody CcBccpCreateRequest request) {
        long manifestId = service.createBccp(user, request);

        CcCreateResponse resp = new CcCreateResponse();
        resp.setManifestId(manifestId);
        return resp;
    }

    @RequestMapping(value = "/core_component/node/{type}/{manifestId}/revision", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CcRevisionResponse getCcNodeRevision(@AuthenticationPrincipal User user,
                                                @PathVariable("type") String type,
                                                @PathVariable("manifestId") long manifestId) {
        switch (type) {
            case "acc":
                return service.getAccNoddRevision(user, manifestId);
            case "bccp":
                return service.getBccpNoddRevision(user, manifestId);
            case "asccp":
                return service.getAsccpNoddRevision(user, manifestId);
            default:
                throw new UnsupportedOperationException();
        }
    }
}