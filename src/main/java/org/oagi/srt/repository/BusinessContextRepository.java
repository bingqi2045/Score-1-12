package org.oagi.srt.repository;

import org.oagi.srt.data.BusinessContext;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class BusinessContextRepository implements SrtRepository<BusinessContext> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_BUSINESS_CONTEXT_STATEMENT = "SELECT `biz_ctx_id`,`guid`,`name`," +
            "`created_by`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp` FROM `biz_ctx`";

    @Override
    public List<BusinessContext> findAll() {
        return jdbcTemplate.queryForList(GET_BUSINESS_CONTEXT_STATEMENT, BusinessContext.class);
    }

    @Override
    public BusinessContext findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_BUSINESS_CONTEXT_STATEMENT)
                .append(" WHERE `biz_ctx_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), BusinessContext.class);
    }

}
