package org.oagi.srt.repository;

import org.oagi.srt.data.BBIE;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class BBIERepository implements SrtRepository<BBIE> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_BBIE_STATEMENT = "SELECT `bbie_id`,`guid`,`based_bcc_id`,`from_abie_id`,`to_bbiep_id`," +
            "`bdt_pri_restri_id`,`code_list_id`,`agency_id_list_id`,`cardinality_min`,`cardinality_max`,`default_value`," +
            "`is_nillable` as nillable,`fixed_value`,`is_null` as nill,`definition`,`remark`," +
            "`created_by`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`seq_key`,`is_used` as used,`owner_top_level_abie_id` FROM `bbie`";

    @Override
    public List<BBIE> findAll() {
        return jdbcTemplate.queryForList(GET_BBIE_STATEMENT, BBIE.class);
    }

    @Override
    public BBIE findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_BBIE_STATEMENT)
                .append(" WHERE `bbie_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), BBIE.class);
    }

    public List<BBIE> findByOwnerTopLevelAbieIdAndUsed(long ownerTopLevelAbieId, boolean used) {
        return jdbcTemplate.queryForList(new StringBuilder(GET_BBIE_STATEMENT)
                        .append(" WHERE `owner_top_level_abie_id` = :owner_top_level_abie_id AND `is_used` = :used").toString(),
                newSqlParameterSource()
                        .addValue("owner_top_level_abie_id", ownerTopLevelAbieId)
                        .addValue("used", used), BBIE.class);
    }

}
