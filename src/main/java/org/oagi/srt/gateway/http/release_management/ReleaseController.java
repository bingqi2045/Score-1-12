package org.oagi.srt.gateway.http.release_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReleaseController {

    @Autowired
    private ReleaseService service;

    @RequestMapping(value = "/simple_releases", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<SimpleRelease> getSimpleReleases() {
        return service.getSimpleReleases();
    }

    @RequestMapping(value = "/simple_release/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public SimpleRelease getSimpleRelease(@PathVariable("id") long releaseId) {
        return service.getSimpleReleaseByReleaseId(releaseId);
    }

}
