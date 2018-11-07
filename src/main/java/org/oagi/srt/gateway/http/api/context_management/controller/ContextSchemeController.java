package org.oagi.srt.gateway.http.api.context_management.controller;

import org.oagi.srt.gateway.http.api.context_management.data.ContextScheme;
import org.oagi.srt.gateway.http.api.context_management.data.SimpleContextScheme;
import org.oagi.srt.gateway.http.api.context_management.data.SimpleContextSchemeValue;
import org.oagi.srt.gateway.http.api.context_management.service.ContextSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ContextSchemeController {

    @Autowired
    private ContextSchemeService service;

    @RequestMapping(value = "/context_schemes", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<ContextScheme> getContextSchemeList() {
        return service.getContextSchemeList();
    }

    @RequestMapping(value = "/context_scheme/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ContextScheme getContextScheme(@PathVariable("id") long id) {return service.getContextScheme(id);
    }

    @RequestMapping(value = "/context_category/{id}/simple_context_schemes", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<SimpleContextScheme> getSimpleContextSchemeList(@PathVariable("id") long ctxCategoryId) {
        return service.getSimpleContextSchemeList(ctxCategoryId);
    }

    @RequestMapping(value = "/context_scheme/{id}/simple_context_scheme_values", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<SimpleContextSchemeValue> getSimpleContextSchemeValueList(@PathVariable("id") long ctxSchemeId) {
        return service.getSimpleContextSchemeValueList(ctxSchemeId);
    }

    @RequestMapping(value = "/context_scheme", method = RequestMethod.PUT)
    public ResponseEntity create(
            @AuthenticationPrincipal User user,
            @RequestBody ContextScheme contextScheme) {
        service.insert(user, contextScheme);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/context_scheme/{id}", method = RequestMethod.POST)
    public ResponseEntity update(
            @PathVariable("id") long id,
            @AuthenticationPrincipal User user,
            @RequestBody ContextScheme contextScheme) {
        contextScheme.setCtxSchemeId(id);
        service.update(user, contextScheme);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/context_scheme/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(
            @PathVariable("id") long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
