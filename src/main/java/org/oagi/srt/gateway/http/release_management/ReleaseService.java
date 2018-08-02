package org.oagi.srt.gateway.http.release_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReleaseService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private String GET_SIMPLE_RELEASES_STATEMENT =
            "SELECT release_id, release_num FROM `release` WHERE state = " + ReleaseState.Final.getValue();

    public List<SimpleRelease> getSimpleReleases() {
        return jdbcTemplate.query(GET_SIMPLE_RELEASES_STATEMENT,
                new BeanPropertyRowMapper(SimpleRelease.class));
    }

    public SimpleRelease getSimpleReleaseByReleaseId(long releaseId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("release_id", releaseId);
        return jdbcTemplate.queryForObject("SELECT release_id, release_num " +
                        "FROM `release` WHERE release_id = :release_id", parameterSource,
                new BeanPropertyRowMapper<>(SimpleRelease.class));
    }
}
