package org.oagi.srt.gateway.http.release_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
}
