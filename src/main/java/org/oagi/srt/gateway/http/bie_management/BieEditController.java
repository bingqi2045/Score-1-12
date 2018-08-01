package org.oagi.srt.gateway.http.bie_management;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oagi.srt.gateway.http.bie_management.bie_edit.*;
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
        return service.getRootNode(topLevelAbieId);
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

    @RequestMapping(value = "/profile_bie/node/bbiep/children", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BieEditNode> getBbiepChildren(@RequestParam("data") String data) {
        BieEditBbiepNode bbiepNode = convertValue(data, BieEditBbiepNode.class);
        return service.getBbiepChildren(bbiepNode);
    }

    @RequestMapping(value = "/profile_bie/node/bbiep/detail", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditAbieBbiepDetail getBbiepDetail(@RequestParam("data") String data) {
        BieEditBbiepNode bbiepNode = convertValue(data, BieEditBbiepNode.class);
        return service.getBbiepDetail(bbiepNode);
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
