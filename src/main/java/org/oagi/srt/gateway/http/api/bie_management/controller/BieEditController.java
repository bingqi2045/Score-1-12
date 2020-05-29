package org.oagi.srt.gateway.http.api.bie_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oagi.srt.data.BieState;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditNode;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditUpdateRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditUpdateResponse;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.CreateExtensionResponse;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.BieEditAbieNode;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.BieEditAsbiepNode;
import org.oagi.srt.gateway.http.api.bie_management.service.BieEditService;
import org.oagi.srt.gateway.http.api.common.data.AccessPrivilege;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repo.component.abie.AbieNode;
import org.oagi.srt.repo.component.asbie.AsbieNode;
import org.oagi.srt.repo.component.asbiep.AsbiepNode;
import org.oagi.srt.repo.component.bbie.BbieNode;
import org.oagi.srt.repo.component.bbie_sc.BbieScNode;
import org.oagi.srt.repo.component.bbiep.BbiepNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.oagi.srt.gateway.http.api.common.data.AccessPrivilege.*;

@RestController
public class BieEditController {

    @Autowired
    private BieEditService service;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/profile_bie/node/root/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BieEditNode getRootNode(@AuthenticationPrincipal User user,
                                   @PathVariable("id") BigInteger topLevelAbieId) {
        BieEditAbieNode rootNode = service.getRootNode(user, topLevelAbieId);
        BieState state = BieState.valueOf((Integer) rootNode.getTopLevelAbieState());
        rootNode.setTopLevelAbieState(state.toString());

        BigInteger userId = sessionService.userId(user);
        AccessPrivilege accessPrivilege = Prohibited;
        switch (state) {
            case Initiating:
                accessPrivilege = Unprepared;
                break;

            case WIP:
                if (userId == rootNode.getOwnerUserId()) {
                    accessPrivilege = CanEdit;
                } else {
                    accessPrivilege = Prohibited;
                }
                break;

            case QA:
                if (userId == rootNode.getOwnerUserId()) {
                    accessPrivilege = CanEdit;
                } else {
                    accessPrivilege = CanView;
                }

                break;

            case Production:
                accessPrivilege = CanView;
                break;
        }

        rootNode.setAccess(accessPrivilege.name());

        return rootNode;
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/abie/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AbieNode getAbieDetail(@AuthenticationPrincipal User user,
                                  @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
                                  @PathVariable("manifestId") BigInteger manifestId,
                                  @RequestParam("hashPath") String hashPath) {
        return service.getAbieDetail(user, topLevelAbieId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/asbie/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AsbieNode getAsbieDetail(@AuthenticationPrincipal User user,
                                    @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
                                    @PathVariable("manifestId") BigInteger manifestId,
                                    @RequestParam("hashPath") String hashPath) {
        return service.getAsbieDetail(user, topLevelAbieId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/bbie/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BbieNode getBbieDetail(@AuthenticationPrincipal User user,
                                  @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
                                  @PathVariable("manifestId") BigInteger manifestId,
                                  @RequestParam("hashPath") String hashPath) {
        return service.getBbieDetail(user, topLevelAbieId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/asbiep/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AsbiepNode getAsbiepDetail(@AuthenticationPrincipal User user,
                                      @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
                                      @PathVariable("manifestId") BigInteger manifestId,
                                      @RequestParam("hashPath") String hashPath) {
        return service.getAsbiepDetail(user, topLevelAbieId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/bbiep/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BbiepNode getBbiepDetail(@AuthenticationPrincipal User user,
                                    @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
                                    @PathVariable("manifestId") BigInteger manifestId,
                                    @RequestParam("hashPath") String hashPath) {
        return service.getBbiepDetail(user, topLevelAbieId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/{topLevelAbieId}/bbie_sc/{manifestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BbieScNode getBbieScDetail(@AuthenticationPrincipal User user,
                                      @PathVariable("topLevelAbieId") BigInteger topLevelAbieId,
                                      @PathVariable("manifestId") BigInteger manifestId,
                                      @RequestParam("hashPath") String hashPath) {
        return service.getBbieScDetail(user, topLevelAbieId, manifestId, hashPath);
    }

    @RequestMapping(value = "/profile_bie/node/root/{id}/state", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateState(@AuthenticationPrincipal User user,
                            @PathVariable("id") BigInteger topLevelAbieId,
                            @RequestBody Map<String, Object> body) {
        BieState state = BieState.valueOf((String) body.get("state"));
        service.updateState(user, topLevelAbieId, state);
    }

    private <T> T convertValue(String data, Class<T> clazz) {
        Map<String, Object> params = new HashMap();
        Arrays.stream(new String(Base64.getDecoder().decode(data)).split("&")).forEach(e -> {
            String[] keyValue = e.split("=");
            if (keyValue.length > 1) {
                params.put(keyValue[0], keyValue[1]);
            } else {
                params.put(keyValue[0], "");
            }

        });
        return objectMapper.convertValue(params, clazz);
    }

    @RequestMapping(value = "/profile_bie/node/detail", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BieEditUpdateResponse updateDetails(@AuthenticationPrincipal User user,
                                               @RequestBody BieEditUpdateRequest request) {

        return service.updateDetails(user, request);
    }

    @RequestMapping(value = "/profile_bie/node/extension/local", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateExtensionResponse createLocalAbieExtension(@AuthenticationPrincipal User user,
                                                            @RequestBody BieEditAsbiepNode extensionNode) {
        CreateExtensionResponse response = service.createLocalAbieExtension(user, extensionNode);
        return response;
    }

    @RequestMapping(value = "/profile_bie/node/extension/global", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateExtensionResponse createGlobalAbieExtension(@AuthenticationPrincipal User user,
                                                             @RequestBody BieEditAsbiepNode extensionNode) {
        CreateExtensionResponse response = service.createGlobalAbieExtension(user, extensionNode);
        return response;
    }
}
