package org.oagi.srt.gateway.http.api.bie_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oagi.srt.gateway.http.api.bie_management.data.BieState;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditBdtPriRestri;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditBdtScPriRestri;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditNode;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.*;
import org.oagi.srt.gateway.http.api.bie_management.service.BieEditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class BieEditController {

    @Autowired
    private BieEditService service;

    @Autowired
    private ObjectMapper objectMapper;

//    public BieTree getBieTree(@PathVariable("id") long topLevelAbieId) {
//        return service.getBieTree(topLevelAbieId);
//    }

    @RequestMapping(value = "/profile_bie/node/root/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditNode getRootNode(@PathVariable("id") long topLevelAbieId) {
        BieEditAbieNode rootNode = service.getRootNode(topLevelAbieId);
        String state = BieState.valueOf((Integer) rootNode.getTopLevelAbieState()).toString();
        rootNode.setTopLevelAbieState(state);
        return rootNode;
    }

    @RequestMapping(value = "/profile_bie/node/abie/children", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BieEditNode> getAbieChildren(@RequestParam("data") String data) {
        BieEditAbieNode abieNode = convertValue(data, BieEditAbieNode.class);
        return service.getAbieChildren(abieNode);
    }

    @RequestMapping(value = "/profile_bie/node/abie/detail", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditAbieNodeDetail getAbieDetail(@RequestParam("data") String data) {
        BieEditAbieNode abieNode = convertValue(data, BieEditAbieNode.class);
        return service.getAbieDetail(abieNode);
    }

    @RequestMapping(value = "/profile_bie/node/asbiep/children", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BieEditNode> getAsbiepChildren(@RequestParam("data") String data) {
        BieEditAsbiepNode asbiepNode = convertValue(data, BieEditAsbiepNode.class);
        return service.getAsbiepChildren(asbiepNode);
    }

    @RequestMapping(value = "/profile_bie/node/asbiep/detail", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditAsbiepNodeDetail getAsbiepDetail(@RequestParam("data") String data) {
        BieEditAsbiepNode asbiepNode = convertValue(data, BieEditAsbiepNode.class);
        return service.getAsbiepDetail(asbiepNode);
    }

    @RequestMapping(value = "/profile_bie/node/bbiep/children", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BieEditNode> getBbiepChildren(@RequestParam("data") String data) {
        BieEditBbiepNode bbiepNode = convertValue(data, BieEditBbiepNode.class);
        return service.getBbiepChildren(bbiepNode);
    }

    @RequestMapping(value = "/profile_bie/node/bbiep/detail", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditBbiepNodeDetail getBbiepDetail(@RequestParam("data") String data) {
        BieEditBbiepNode bbiepNode = convertValue(data, BieEditBbiepNode.class);
        return service.getBbiepDetail(bbiepNode);
    }

    @RequestMapping(value = "/profile_bie/node/bbiep/pri_restri", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditBdtPriRestri getBdtPriRestri(@RequestParam("data") String data) {
        BieEditBbiepNode bbiepNode = convertValue(data, BieEditBbiepNode.class);
        return service.getBdtPriRestri(bbiepNode);
    }

    @RequestMapping(value = "/profile_bie/node/bbie_sc/detail", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditBbieScNodeDetail getBbieScDetail(@RequestParam("data") String data) {
        BieEditBbieScNode bbieScNode = convertValue(data, BieEditBbieScNode.class);
        return service.getBbieScDetail(bbieScNode);
    }

    @RequestMapping(value = "/profile_bie/node/bbie_sc/pri_restri", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditBdtScPriRestri getBdtScPriRestri(@RequestParam("data") String data) {
        BieEditBbieScNode bbieScNode = convertValue(data, BieEditBbieScNode.class);
        return service.getBdtScPriRestri(bbieScNode);
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
