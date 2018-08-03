package org.oagi.srt.gateway.http.release_management;

import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class ReleaseService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    public List<SimpleRelease> getSimpleReleases() {
        return jdbcTemplate.queryForList("SELECT release_id, release_num FROM `release` WHERE state = " + ReleaseState.Final.getValue(),
                SimpleRelease.class);
    }

    public SimpleRelease getSimpleReleaseByReleaseId(long releaseId) {
        return jdbcTemplate.queryForObject("SELECT release_id, release_num " +
                        "FROM `release` WHERE release_id = :release_id",
                newSqlParameterSource().addValue("release_id", releaseId),
                SimpleRelease.class);
    }
}
