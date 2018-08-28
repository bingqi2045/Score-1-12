package org.oagi.srt.repository;

import org.oagi.srt.data.CodeListValue;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class CodeListValueRepository implements SrtRepository<CodeListValue> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_CODE_LIST_VALUE_STATEMENT = "SELECT `code_list_value_id`,`code_list_id`,`value`,`name`," +
            "`definition`,`definition_source`,`used_indicator`,`locked_indicator`,`extension_Indicator` " +
            "FROM `code_list_value`";

    @Override
    public List<CodeListValue> findAll() {
        return jdbcTemplate.queryForList(GET_CODE_LIST_VALUE_STATEMENT, CodeListValue.class);
    }

    @Override
    public CodeListValue findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_CODE_LIST_VALUE_STATEMENT)
                .append(" WHERE `code_list_value_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), CodeListValue.class);
    }

}
