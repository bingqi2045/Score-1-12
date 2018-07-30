package org.oagi.srt.gateway.http.bie_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BieListController {

    @Autowired
    private BieService service;

    @RequestMapping(value = "/profile_bie_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<BieList> getBieList() {
        List<BieList> bieLists = service.getBieList();
        bieLists.forEach(e -> {
            int state = (Integer) e.getState();
            e.setState(BieState.valueOf(state).name());
        });
        return bieLists;
    }

    @RequestMapping(value = "/profile_bie_list/delete", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteBieList(@RequestBody DeleteBieListRequest request) {
        List<Long> topLevelAbieIds = request.getTopLevelAbieIds();
        if (!topLevelAbieIds.isEmpty()) {
            service.deleteBieList(topLevelAbieIds);
        }
        return ResponseEntity.noContent().build();
    }
}
