package org.oagi.srt.repository;

import org.oagi.srt.data.Release;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class ReleaseRepository implements SrtRepository<Release> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_RELEASE_STATEMENT = "SELECT `release_id`,`release_num`,`release_note`,`namespace_id`," +
            "`created_by`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`,`state` FROM `release`";

    @Override
    public List<Release> findAll() {
        return jdbcTemplate.queryForList(GET_RELEASE_STATEMENT, Release.class);
    }

    @Override
    public Release findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_RELEASE_STATEMENT)
                .append(" WHERE `release_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), Release.class);
    }

}
