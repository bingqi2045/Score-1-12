package org.oagi.srt.repository;

import org.oagi.srt.data.BusinessContextValue;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class BizCtxValueRepository implements SrtRepository<BusinessContextValue> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_BUSINESS_CONTEXT_VALUE_STATEMENT = "SELECT `biz_ctx_value_id`,`biz_ctx_id`,`ctx_scheme_value_id` " +
            "FROM `biz_ctx_value`";

    @Override
    public List<BusinessContextValue> findAll() {
        return jdbcTemplate.queryForList(GET_BUSINESS_CONTEXT_VALUE_STATEMENT, BusinessContextValue.class);
    }

    @Override
    public BusinessContextValue findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_BUSINESS_CONTEXT_VALUE_STATEMENT)
                .append(" WHERE `biz_ctx_value_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), BusinessContextValue.class);
    }

    public List<BusinessContextValue> findByBizCtxId(long bizCtxId) {
        return jdbcTemplate.queryForList(new StringBuilder(GET_BUSINESS_CONTEXT_VALUE_STATEMENT)
                .append(" WHERE `biz_ctx_id` = :biz_ctx_id").toString(), newSqlParameterSource()
                .addValue("biz_ctx_id", bizCtxId), BusinessContextValue.class);
    }

}
