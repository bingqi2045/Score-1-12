package org.oagi.srt.gateway.http.context_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BusinessContextController {

    @Autowired
    private BusinessContextService service;

    @RequestMapping(value = "/business_contexts", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BusinessContext> getBusinessContextList() {
        return service.getBusinessContextList();
    }

    @RequestMapping(value = "/business_context/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BusinessContext getBusinessContext(@PathVariable("id") long id) {
        return service.getBusinessContext(id);
    }

    @RequestMapping(value = "/simple_business_contexts", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<SimpleBusinessContext> getSimpleBusinessContextList() {
        return service.getSimpleBusinessContextList();
    }

    @RequestMapping(value = "/simple_business_context/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public SimpleBusinessContext getSimpleBusinessContext(@PathVariable("id") long id) {
        return service.getSimpleBusinessContext(id);
    }

    @RequestMapping(value = "/business_context", method = RequestMethod.PUT)
    public ResponseEntity create(
            @AuthenticationPrincipal User user,
            @RequestBody BusinessContext businessContext) {
        service.insert(user, businessContext);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/business_context/{id}", method = RequestMethod.POST)
    public ResponseEntity update(
            @PathVariable("id") long id,
            @AuthenticationPrincipal User user,
            @RequestBody BusinessContext businessContext) {
        businessContext.setBizCtxId(id);
        service.update(user, businessContext);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/business_context/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(
            @PathVariable("id") long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
