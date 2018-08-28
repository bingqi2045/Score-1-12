package org.oagi.srt.repository;

import org.oagi.srt.data.AgencyIdList;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class AgencyIdListRepository implements SrtRepository<AgencyIdList> {
    
    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_AGENCY_ID_LIST_STATEMENT = "SELECT `agency_id_list_id`,`guid`,`enum_type_guid`,`name`," +
            "`list_id`,`agency_id_list_value_id`,`version_id`,`module_id`,`definition` FROM `agency_id_list`";

    @Override
    public List<AgencyIdList> findAll() {
        return jdbcTemplate.queryForList(GET_AGENCY_ID_LIST_STATEMENT, AgencyIdList.class);
    }

    @Override
    public AgencyIdList findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_AGENCY_ID_LIST_STATEMENT)
                .append(" WHERE `agency_id_list_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), AgencyIdList.class);
    }
    
}
