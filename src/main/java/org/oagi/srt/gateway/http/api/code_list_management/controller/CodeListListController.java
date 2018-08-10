package org.oagi.srt.gateway.http.api.code_list_management.controller;

import org.oagi.srt.gateway.http.api.code_list_management.data.CodeList;
import org.oagi.srt.gateway.http.api.code_list_management.service.CodeListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CodeListListController {

    @Autowired
    private CodeListService service;

    @RequestMapping(value = "/code_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<CodeList> getCodeListList() {
        return service.getCodeListList();
    }
}
