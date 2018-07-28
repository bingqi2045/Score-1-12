package org.oagi.srt.gateway.http.context_management;

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
    public ContextScheme getContextScheme(@PathVariable("id") long id) {
        return service.getContextScheme(id);
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
