package org.oagi.srt.repository;

import org.oagi.srt.data.ABIE;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class ABIERepository implements SrtRepository<ABIE> {
    
    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_ABIE_STATEMENT = "SELECT `abie_id`,`guid`,`based_acc_id`,`biz_ctx_id`,`definition`," +
            "`created_by`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`client_id`,`version`,`status`,`remark`,`biz_term`,`owner_top_level_abie_id` FROM `abie`";

    @Override
    public List<ABIE> findAll() {
        return jdbcTemplate.queryForList(GET_ABIE_STATEMENT, ABIE.class);
    }

    @Override
    public ABIE findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_ABIE_STATEMENT)
                .append(" WHERE `abie_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), ABIE.class);
    }

    public List<ABIE> findByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList(new StringBuilder(GET_ABIE_STATEMENT)
                .append(" WHERE `owner_top_level_abie_id` = :owner_top_level_abie_id").toString(), newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), ABIE.class);
    }

}
