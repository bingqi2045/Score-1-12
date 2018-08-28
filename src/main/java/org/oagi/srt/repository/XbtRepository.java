package org.oagi.srt.repository;

import org.oagi.srt.data.Xbt;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class XbtRepository implements SrtRepository<Xbt> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_XBT_STATEMENT = "SELECT `xbt_id`,`name`,`builtIn_type`,`jbt_draft05_map`,`subtype_of_xbt_id`," +
            "`schema_definition`,`module_id`,`release_id`,`revision_doc`,`state`," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`revision_num`,`revision_tracking_num`,`revision_action`,`current_xbt_id`,`is_deprecated` as deprecated FROM `xbt`";

    @Override
    public List<Xbt> findAll() {
        return jdbcTemplate.queryForList(GET_XBT_STATEMENT, Xbt.class);
    }

    @Override
    public Xbt findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_XBT_STATEMENT)
                .append(" WHERE `xbt_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), Xbt.class);
    }

}
