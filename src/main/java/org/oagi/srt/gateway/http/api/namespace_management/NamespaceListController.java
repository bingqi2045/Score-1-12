package org.oagi.srt.gateway.http.api.namespace_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NamespaceListController {

    @Autowired
    private NamespaceListService service;

    @RequestMapping(value = "/namespace_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<NamespaceList> getNamespaceList() {
        return service.getNamespaceList();
    }
}
