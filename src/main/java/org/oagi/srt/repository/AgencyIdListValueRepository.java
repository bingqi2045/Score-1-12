package org.oagi.srt.repository;

import org.oagi.srt.data.AgencyIdListValue;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class AgencyIdListValueRepository implements SrtRepository<AgencyIdListValue> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_AGENCY_ID_LIST_VALUE_STATEMENT = "SELECT `agency_id_list_value_id`,`value`,`name`," +
            "`definition`,`owner_list_id` FROM `agency_id_list_value`";

    @Override
    public List<AgencyIdListValue> findAll() {
        return jdbcTemplate.queryForList(GET_AGENCY_ID_LIST_VALUE_STATEMENT, AgencyIdListValue.class);
    }

    @Override
    public AgencyIdListValue findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_AGENCY_ID_LIST_VALUE_STATEMENT)
                .append(" WHERE `agency_id_list_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), AgencyIdListValue.class);
    }

}
