package org.oagi.srt.gateway.http.api.cc_management.repository;

import org.oagi.srt.gateway.http.api.cc_management.data.BdtPriRestri;
import org.oagi.srt.gateway.http.api.cc_management.data.BdtScPriRestri;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class BdtRepository {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    public List<BdtPriRestri> getBdtPriRestriListByBdtId(long bdtId) {
        return jdbcTemplate.queryForList("SELECT bdt_pri_restri_id, bdt_id, cdt_awd_pri_xps_type_map_id, " +
                "code_list_id, agency_id_list_id, is_default FROM bdt_pri_restri WHERE bdt_id = :bdt_id", newSqlParameterSource()
                .addValue("bdt_id", bdtId), BdtPriRestri.class);
    }

    public List<BdtScPriRestri> getBdtScPriRestriListByBdtId(long bdtScId) {
        return jdbcTemplate.queryForList("SELECT bdt_sc_pri_restri_id, bdt_sc_id, cdt_sc_awd_pri_xps_type_map_id, " +
                "code_list_id, agency_id_list_id, is_default FROM bdt_pri_restri WHERE bdt_sc_id = :bdt_sc_id", newSqlParameterSource()
                .addValue("bdt_sc_id", bdtScId), BdtScPriRestri.class);
    }
}
