package org.oagi.srt.gateway.http.api.code_list_management.service;

import org.oagi.srt.gateway.http.api.code_list_management.data.CodeList;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CodeListService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    public List<CodeList> getCodeListList() {
        return jdbcTemplate.queryForList("SELECT c.code_list_id, c.name as code_list_name, " +
                "c.based_code_list_id, b.name as based_code_list_name, " +
                "c.agency_id, a.name as agency_id_name, c.version_id, c.last_update_timestamp, " +
                "c.extensible_indicator as extensible, c.state " +
                "FROM code_list c " +
                "LEFT JOIN code_list b ON c.based_code_list_id = b.code_list_id " +
                "LEFT JOIN agency_id_list_value a ON c.agency_id = a.agency_id_list_value_id", CodeList.class);
    }
}
