package org.oagi.srt.repository;

import org.oagi.srt.data.BizCtx;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class BizCtxRepository implements SrtRepository<BizCtx> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_BUSINESS_CONTEXT_STATEMENT = "SELECT `biz_ctx_id`,`guid`,`name`," +
            "`created_by`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp` FROM `biz_ctx`";

    @Override
    public List<BizCtx> findAll() {
        return jdbcTemplate.queryForList(GET_BUSINESS_CONTEXT_STATEMENT, BizCtx.class);
    }

    @Override
    public BizCtx findById(long id) {
        if (id <= 0L) {
            return null;
        }
        return jdbcTemplate.queryForObject(new StringBuilder(GET_BUSINESS_CONTEXT_STATEMENT)
                .append(" WHERE `biz_ctx_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), BizCtx.class);
    }

}
