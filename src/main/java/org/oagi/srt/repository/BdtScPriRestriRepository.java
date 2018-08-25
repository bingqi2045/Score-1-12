package org.oagi.srt.repository;

import org.oagi.srt.data.BdtPriRestri;
import org.oagi.srt.data.BdtScPriRestri;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class BdtScPriRestriRepository implements SrtRepository<BdtScPriRestri> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_BDT_SC_PRI_RESTRI_STATEMENT = "SELECT `bdt_sc_pri_restri_id`,`bdt_sc_id`," +
            "`cdt_sc_awd_pri_xps_type_map_id`,`code_list_id`,`agency_id_list_id`,`is_default` as defaulted " +
            "FROM `bdt_sc_pri_restri`";

    @Override
    public List<BdtScPriRestri> findAll() {
        return jdbcTemplate.queryForList(GET_BDT_SC_PRI_RESTRI_STATEMENT, BdtScPriRestri.class);
    }

    @Override
    public BdtScPriRestri findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_BDT_SC_PRI_RESTRI_STATEMENT)
                .append(" WHERE `bdt_sc_pri_restri` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), BdtScPriRestri.class);
    }
}
