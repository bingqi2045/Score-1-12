package org.oagi.srt.repository;

import org.oagi.srt.data.BBIESC;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class BBIESCRepository implements SrtRepository<BBIESC> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_BBIESC_STATEMENT = "SELECT `bbie_sc_id`,`guid`,`bbie_id`,`dt_sc_id`," +
            "`dt_sc_pri_restri_id`,`code_list_id`,`agency_id_list_id`,`cardinality_min`,`cardinality_max`," +
            "`default_value`,`fixed_value`,`definition`,`remark`,`biz_term`,`is_used` as used," +
            "`owner_top_level_abie_id` FROM `bbie_sc`";

    @Override
    public List<BBIESC> findAll() {
        return jdbcTemplate.queryForList(GET_BBIESC_STATEMENT, BBIESC.class);
    }

    @Override
    public BBIESC findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_BBIESC_STATEMENT)
                .append(" WHERE `bbie_sc_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), BBIESC.class);
    }

    public List<BBIESC> findByOwnerTopLevelAbieIdAndUsed(long ownerTopLevelAbieId, boolean used) {
        return jdbcTemplate.queryForList(new StringBuilder(GET_BBIESC_STATEMENT)
                        .append(" WHERE `owner_top_level_abie_id` = :owner_top_level_abie_id AND `is_used` = :used").toString(),
                newSqlParameterSource()
                        .addValue("owner_top_level_abie_id", ownerTopLevelAbieId)
                        .addValue("used", used), BBIESC.class);
    }

}
