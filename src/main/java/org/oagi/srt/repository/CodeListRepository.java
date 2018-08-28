package org.oagi.srt.repository;

import org.oagi.srt.data.CodeList;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class CodeListRepository implements SrtRepository<CodeList> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_CODE_LIST_STATEMENT = "SELECT `code_list_id`,`guid`,`enum_type_guid`,`name`,`list_id`," +
            "`agency_id`,`version_id`,`definition`,`remark`,`definition_source`,`based_code_list_id`," +
            "`extensible_indicator`,`module_id`," +
            "`created_by`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`,`state` FROM `code_list`";

    @Override
    public List<CodeList> findAll() {
        return jdbcTemplate.queryForList(GET_CODE_LIST_STATEMENT, CodeList.class);
    }

    @Override
    public CodeList findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_CODE_LIST_STATEMENT)
                .append(" WHERE `code_list_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), CodeList.class);
    }

}
