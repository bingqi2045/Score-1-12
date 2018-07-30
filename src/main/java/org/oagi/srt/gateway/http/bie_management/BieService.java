package org.oagi.srt.gateway.http.bie_management;

import org.oagi.srt.gateway.http.cc_management.CcState;
import org.oagi.srt.gateway.http.cc_management.CcUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional(readOnly = true)
public class BieService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    private String GET_ASCCP_LIST_FOR_BIE_STATEMENT =
            "SELECT asccp_id, guid, property_term, module_id, state, " +
                    "revision_num, revision_tracking_num, revision_action, release_id, last_update_timestamp " +
                    "FROM asccp WHERE revision_num > 0 AND state = " + CcState.Published.getValue();

    public List<AsccpForBie> getAsccpListForBie(long releaseId) {
        List<AsccpForBie> asccpForBieList =
                jdbcTemplate.query(GET_ASCCP_LIST_FOR_BIE_STATEMENT,
                        new BeanPropertyRowMapper(AsccpForBie.class));

        Map<String, List<AsccpForBie>> groupingByGuidAsccpForBieList =
                asccpForBieList.stream()
                        .collect(groupingBy(AsccpForBie::getGuid));

        asccpForBieList = groupingByGuidAsccpForBieList.values().stream()
                .map(e -> CcUtility.getLatestEntity(releaseId, e))
                .filter(e -> e != null)
                .collect(Collectors.toList());

        return asccpForBieList;
    }


    private String GET_BIE_LIST_STATEMENT =
            "";

    public List<BieList> getBieList() {
        return Collections.emptyList();
    }
}
