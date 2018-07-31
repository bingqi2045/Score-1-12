package org.oagi.srt.gateway.http.bie_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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

    @RequestMapping(value = "/profile_bie/node/{type}/{top_level_abie_id}/{cc_id}/children", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BieEditNode> getChildren(@PathVariable("type") String type,
                                         @PathVariable("top_level_abie_id") long topLevelAbieId,
                                         @PathVariable("cc_id") long ccId,
                                         @RequestParam("bie_id") Long bieId) {

        BieEditNode node = new BieEditNode();
        node.setTopLevelAbieId(topLevelAbieId);
        node.setType(type);
        node.setBieId(bieId);
        node.setCcId(ccId);

        return service.getChildren(node);
    }

}
