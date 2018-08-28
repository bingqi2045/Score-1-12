package org.oagi.srt.repository;

import org.oagi.srt.data.ASBIEP;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class ASBIEPRepository implements SrtRepository<ASBIEP> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_ASBIEP_STATEMENT = "SELECT `asbiep_id`,`guid`,`based_asccp_id`,`role_of_abie_id`," +
            "`definition`,`remark`,`biz_term`," +
            "`created_by`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`owner_top_level_abie_id` FROM `asbiep`";

    @Override
    public List<ASBIEP> findAll() {
        return jdbcTemplate.queryForList(GET_ASBIEP_STATEMENT, ASBIEP.class);
    }

    @Override
    public ASBIEP findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_ASBIEP_STATEMENT)
                .append(" WHERE `asbiep_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), ASBIEP.class);
    }

    public List<ASBIEP> findByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList(new StringBuilder(GET_ASBIEP_STATEMENT)
                .append(" WHERE `owner_top_level_abie_id` = :owner_top_level_abie_id").toString(), newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), ASBIEP.class);
    }

}
