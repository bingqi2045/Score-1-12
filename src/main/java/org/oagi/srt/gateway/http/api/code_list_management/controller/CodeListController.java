package org.oagi.srt.gateway.http.api.code_list_management.controller;

import org.oagi.srt.gateway.http.api.code_list_management.data.CodeList;
import org.oagi.srt.gateway.http.api.code_list_management.data.CodeListForList;
import org.oagi.srt.gateway.http.api.code_list_management.data.GetCodeListsFilter;
import org.oagi.srt.gateway.http.api.code_list_management.service.CodeListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CodeListController {

    @Autowired
    private CodeListService service;

    @RequestMapping(value = "/code_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<CodeListForList> getCodeLists(
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "extensible", required = false) Boolean extensible) {

        GetCodeListsFilter filter = new GetCodeListsFilter();
        filter.setState(state);
        filter.setExtensible(extensible);

        return service.getCodeLists(filter);
    }

    @RequestMapping(value = "/code_list/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CodeList getCodeList(@PathVariable("id") long id) {
        return service.getCodeList(id);
    }

    @RequestMapping(value = "/code_list", method = RequestMethod.PUT)
    public ResponseEntity create(
            @AuthenticationPrincipal User user,
            @RequestBody CodeList codeList) {
        service.insert(user, codeList);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/code_list/{id}", method = RequestMethod.POST)
    public ResponseEntity update(
            @PathVariable("id") long id,
            @AuthenticationPrincipal User user,
            @RequestBody CodeList codeList) {
        codeList.setCodeListId(id);
        service.update(user, codeList);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/code_list/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(
            @PathVariable("id") long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
