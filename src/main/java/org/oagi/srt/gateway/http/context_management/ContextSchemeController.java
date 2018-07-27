package org.oagi.srt.gateway.http.context_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value = "/context_scheme", method = RequestMethod.PUT)
    public ResponseEntity create(
            @AuthenticationPrincipal User user,
            @RequestBody ContextScheme contextScheme) {
        service.insert(user, contextScheme);
        return ResponseEntity.noContent().build();
    }
}
