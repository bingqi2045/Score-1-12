package org.oagi.srt.gateway.http.api.bie_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oagi.srt.data.BieState;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditNode;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditUpdateRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditUpdateResponse;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.CreateExtensionResponse;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.*;
import org.oagi.srt.gateway.http.api.bie_management.service.BieEditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class BieEditController {

    @Autowired
    private BieEditService service;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/profile_bie/node/root/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditNode getRootNode(@AuthenticationPrincipal User user,
                                   @PathVariable("id") long topLevelAbieId) {
        BieEditAbieNode rootNode = service.getRootNode(user, topLevelAbieId);
        String state = BieState.valueOf((Integer) rootNode.getTopLevelAbieState()).toString();
        rootNode.setTopLevelAbieState(state);
        return rootNode;
    }

    @RequestMapping(value = "/profile_bie/node/root/{id}/state", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void updateState(@AuthenticationPrincipal User user,
                            @PathVariable("id") long topLevelAbieId,
                            @RequestBody Map<String, Object> body) {
        BieState state = BieState.valueOf((String) body.get("state"));
        service.updateState(user, topLevelAbieId, state);
    }

    @RequestMapping(value = "/profile_bie/node/children/abie", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BieEditNode> getAbieChildren(@AuthenticationPrincipal User user,
                                             @RequestParam("data") String data) {
        BieEditAbieNode abieNode = convertValue(data, BieEditAbieNode.class);
        return service.getDescendants(user, abieNode);
    }

    @RequestMapping(value = "/profile_bie/node/detail/abie", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditNodeDetail getAbieDetail(@AuthenticationPrincipal User user,
                                           @RequestParam("data") String data) {
        BieEditAbieNode abieNode = convertValue(data, BieEditAbieNode.class);
        return service.getDetail(user, abieNode);
    }

    @RequestMapping(value = "/profile_bie/node/children/asbiep", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BieEditNode> getAsbiepChildren(@AuthenticationPrincipal User user,
                                               @RequestParam("data") String data) {
        BieEditAsbiepNode asbiepNode = convertValue(data, BieEditAsbiepNode.class);
        return service.getDescendants(user, asbiepNode);
    }

    @RequestMapping(value = "/profile_bie/node/detail/asbiep", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditNodeDetail getAsbiepDetail(@AuthenticationPrincipal User user,
                                             @RequestParam("data") String data) {
        BieEditAsbiepNode asbiepNode = convertValue(data, BieEditAsbiepNode.class);
        return service.getDetail(user, asbiepNode);
    }

    @RequestMapping(value = "/profile_bie/node/children/bbiep", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BieEditNode> getBbiepChildren(@AuthenticationPrincipal User user,
                                              @RequestParam("data") String data) {
        BieEditBbiepNode bbiepNode = convertValue(data, BieEditBbiepNode.class);
        return service.getDescendants(user, bbiepNode);
    }

    @RequestMapping(value = "/profile_bie/node/detail/bbiep", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditNodeDetail getBbiepDetail(@AuthenticationPrincipal User user,
                                            @RequestParam("data") String data) {
        BieEditBbiepNode bbiepNode = convertValue(data, BieEditBbiepNode.class);
        return service.getDetail(user, bbiepNode);
    }

    @RequestMapping(value = "/profile_bie/node/detail/bbie_sc", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditNodeDetail getBbieScDetail(@AuthenticationPrincipal User user,
                                             @RequestParam("data") String data) {
        BieEditBbieScNode bbieScNode = convertValue(data, BieEditBbieScNode.class);
        return service.getDetail(user, bbieScNode);
    }

    private <T> T convertValue(String data, Class<T> clazz) {
        Map<String, Object> params = new HashMap();
        Arrays.stream(new String(Base64.getDecoder().decode(data)).split("&")).forEach(e -> {
            String[] keyValue = e.split("=");
            params.put(keyValue[0], keyValue[1]);
        });
        return objectMapper.convertValue(params, clazz);
    }

    @RequestMapping(value = "/profile_bie/node/detail", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditUpdateResponse updateDetails(@AuthenticationPrincipal User user,
                                               @RequestBody BieEditUpdateRequest request) {

        return service.updateDetails(user, request);
    }

    @RequestMapping(value = "/profile_bie/node/extension/local", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CreateExtensionResponse createLocalAbieExtension(@AuthenticationPrincipal User user,
                                                            @RequestBody BieEditAsbiepNode extensionNode) {
        long extensionId = service.createLocalAbieExtension(user, extensionNode);
        CreateExtensionResponse response = new CreateExtensionResponse();
        response.setExtensionId(extensionId);
        return response;
    }

    @RequestMapping(value = "/profile_bie/node/extension/global", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CreateExtensionResponse createGlobalAbieExtension(@AuthenticationPrincipal User user,
                                                             @RequestBody BieEditAsbiepNode extensionNode) {
        long extensionId = service.createGlobalAbieExtension(user, extensionNode);
        CreateExtensionResponse response = new CreateExtensionResponse();
        response.setExtensionId(extensionId);
        return response;
    }

}
