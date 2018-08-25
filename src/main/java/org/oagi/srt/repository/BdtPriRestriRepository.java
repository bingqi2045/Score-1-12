package org.oagi.srt.repository;

import org.oagi.srt.data.BdtPriRestri;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class BdtPriRestriRepository implements SrtRepository<BdtPriRestri> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_BDT_PRI_RESTRI_STATEMENT = "SELECT `bdt_pri_restri_id`,`bdt_id`," +
            "`cdt_awd_pri_xps_type_map_id`,`code_list_id`,`agency_id_list_id`,`is_default` as defaulted " +
            "FROM `bdt_pri_restri`";

    @Override
    public List<BdtPriRestri> findAll() {
        return jdbcTemplate.queryForList(GET_BDT_PRI_RESTRI_STATEMENT, BdtPriRestri.class);
    }

    @Override
    public BdtPriRestri findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_BDT_PRI_RESTRI_STATEMENT)
                .append(" WHERE `bdt_pri_restri_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), BdtPriRestri.class);
    }
}
