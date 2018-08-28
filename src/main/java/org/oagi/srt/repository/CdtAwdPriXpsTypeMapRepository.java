package org.oagi.srt.repository;

import org.oagi.srt.data.CdtAwdPriXpsTypeMap;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class CdtAwdPriXpsTypeMapRepository implements SrtRepository<CdtAwdPriXpsTypeMap> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_CDT_AWD_PRI_XPS_TYPE_MAP_STATEMENT = "SELECT `cdt_awd_pri_xps_type_map_id`,`cdt_awd_pri_id`,`xbt_id` " +
            "FROM `cdt_awd_pri_xps_type_map`";

    @Override
    public List<CdtAwdPriXpsTypeMap> findAll() {
        return jdbcTemplate.queryForList(GET_CDT_AWD_PRI_XPS_TYPE_MAP_STATEMENT, CdtAwdPriXpsTypeMap.class);
    }

    @Override
    public CdtAwdPriXpsTypeMap findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_CDT_AWD_PRI_XPS_TYPE_MAP_STATEMENT)
                .append(" WHERE `cdt_awd_pri_xps_type_map_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), CdtAwdPriXpsTypeMap.class);
    }

}
