package org.oagi.srt.repository;

import org.oagi.srt.data.CdtScAwdPriXpsTypeMap;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class CdtScAwdPriXpsTypeMapRepository implements SrtRepository<CdtScAwdPriXpsTypeMap> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_CDT_SC_AWD_PRI_XPS_TYPE_MAP_STATEMENT = "SELECT `cdt_sc_awd_pri_xps_type_map_id`,`cdt_sc_awd_pri_id`," +
            "`xbt_id` FROM `cdt_sc_awd_pri_xps_type_map`";

    @Override
    public List<CdtScAwdPriXpsTypeMap> findAll() {
        return jdbcTemplate.queryForList(GET_CDT_SC_AWD_PRI_XPS_TYPE_MAP_STATEMENT, CdtScAwdPriXpsTypeMap.class);
    }

    @Override
    public CdtScAwdPriXpsTypeMap findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_CDT_SC_AWD_PRI_XPS_TYPE_MAP_STATEMENT)
                .append(" WHERE `cdt_sc_awd_pri_xps_type_map_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), CdtScAwdPriXpsTypeMap.class);
    }

}
