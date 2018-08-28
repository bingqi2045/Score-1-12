package org.oagi.srt.repository;

import org.oagi.srt.data.BBIEP;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class BBIEPRepository implements SrtRepository<BBIEP> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_BBIEP_STATEMENT = "SELECT `bbiep_id`,`guid`,`based_bccp_id`,`definition`,`remark`,`biz_term`," +
            "`created_by`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`owner_top_level_abie_id` FROM `bbiep`";

    @Override
    public List<BBIEP> findAll() {
        return jdbcTemplate.queryForList(GET_BBIEP_STATEMENT, BBIEP.class);
    }

    @Override
    public BBIEP findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_BBIEP_STATEMENT)
                .append(" WHERE `bbiep_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), BBIEP.class);
    }

    public List<BBIEP> findByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList(new StringBuilder(GET_BBIEP_STATEMENT)
                .append(" WHERE `owner_top_level_abie_id` = :owner_top_level_abie_id").toString(), newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), BBIEP.class);
    }

}
