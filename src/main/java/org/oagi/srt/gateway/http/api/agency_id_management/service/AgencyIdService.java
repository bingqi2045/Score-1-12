package org.oagi.srt.gateway.http.api.agency_id_management.service;

import org.oagi.srt.gateway.http.api.agency_id_management.data.SimpleAgencyIdListValue;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AgencyIdService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    public List<SimpleAgencyIdListValue> getSimpleAgencyIdListValues() {
        return jdbcTemplate.queryForList("SELECT agency_id_list_value_id, name " +
                "FROM agency_id_list_value", SimpleAgencyIdListValue.class);
    }
}
