package org.oagi.srt.gateway.http.bie_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class BieService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private String GET_BIE_LIST_STATEMENT =
            "";

    public List<BieList> getBieList() {
        return Collections.emptyList();
    }
}
