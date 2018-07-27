package org.oagi.srt.gateway.http.bie_management;

import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class BieListController {

    public List<BieList> getBieList() {
        return Collections.emptyList();
    }
}
