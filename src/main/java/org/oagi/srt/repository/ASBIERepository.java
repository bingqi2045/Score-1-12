package org.oagi.srt.repository;

import org.oagi.srt.data.ASBIE;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class ASBIERepository implements SrtRepository<ASBIE> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_ASBIE_STATEMENT = "SELECT `asbie_id`,`guid`,`from_abie_id`,`to_asbiep_id`,`based_ascc_id`," +
            "`definition`,`cardinality_min`,`cardinality_max`,`is_nillable` as nillable,`remark`," +
            "`created_by`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`seq_key`,`is_used` as used,`owner_top_level_abie_id` FROM `asbie`";

    @Override
    public List<ASBIE> findAll() {
        return jdbcTemplate.queryForList(GET_ASBIE_STATEMENT, ASBIE.class);
    }

    @Override
    public ASBIE findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_ASBIE_STATEMENT)
                .append(" WHERE `asbie_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), ASBIE.class);
    }

    public List<ASBIE> findByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList(new StringBuilder(GET_ASBIE_STATEMENT)
                        .append(" WHERE `owner_top_level_abie_id` = :owner_top_level_abie_id").toString(),
                newSqlParameterSource()
                        .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), ASBIE.class);
    }

}
