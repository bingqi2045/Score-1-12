package org.oagi.srt.gateway.http.bie_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BieEditController {

    @Autowired
    private BieEditService service;

    @RequestMapping(value = "/profile_bie/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BieEditNode getRootNode(@PathVariable("id") long topLevelAbieId) {
        return service.getRootNode(topLevelAbieId);
    }
}
