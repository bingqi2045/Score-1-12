package org.oagi.srt.gateway.http.bie_management;

import org.oagi.srt.gateway.http.bie_management.bie_edit.BieEditAbieNode;
import org.oagi.srt.gateway.http.bie_management.bie_edit.BieEditAsbiepNode;
import org.oagi.srt.gateway.http.bie_management.bie_edit.BieEditBbiepNode;
import org.oagi.srt.gateway.http.bie_management.bie_edit.BieEditNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BieEditController {

    @Autowired
    private BieEditService service;


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
    public List<BieEditNode> getChildren(BieEditAbieNode abieNode) {
        return service.getAbieChildren(abieNode);
    }

    @RequestMapping(value = "/profile_bie/node/asbiep/children", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BieEditNode> getChildren(BieEditAsbiepNode asbiepNode) {
        return service.getAsbiepChildren(asbiepNode);
    }

    @RequestMapping(value = "/profile_bie/node/bbiep/children", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BieEditNode> getChildren(BieEditBbiepNode bbiepNode) {
        return service.getBbiepChildren(bbiepNode);
    }

}
