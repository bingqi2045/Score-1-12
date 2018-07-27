package org.oagi.srt.gateway.http.context_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ContextCategoryController {

    @Autowired
    private ContextCategoryService service;

    @RequestMapping(value = "/context_categories", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<ContextCategory> getContextCategoryList() {
        return service.getContextCategoryList();
    }

    @RequestMapping(value = "/simple_context_categories", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<SimpleContextCategory> getSimpleContextCategoryList() {
        return service.getSimpleContextCategoryList();
    }


    @RequestMapping(value = "/context_category/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ContextCategory getContextCategory(@PathVariable("id") long id) {
        return service.getContextCategory(id);
    }

    @RequestMapping(value = "/context_category", method = RequestMethod.PUT)
    public ResponseEntity create(
            @RequestBody ContextCategory contextCategory) {
        service.insert(contextCategory);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/context_category/{id}", method = RequestMethod.POST)
    public ResponseEntity update(
            @PathVariable("id") long id,
            @RequestBody ContextCategory contextCategory) {
        contextCategory.setCtxCategoryId(id);
        service.update(contextCategory);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/context_category/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(
            @PathVariable("id") long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
