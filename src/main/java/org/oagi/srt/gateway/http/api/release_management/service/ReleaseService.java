package org.oagi.srt.gateway.http.api.release_management.service;

import org.oagi.srt.gateway.http.api.release_management.data.ReleaseList;
import org.oagi.srt.gateway.http.api.release_management.data.ReleaseState;
import org.oagi.srt.gateway.http.api.release_management.data.SimpleRelease;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class ReleaseService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    public List<SimpleRelease> getSimpleReleases() {
        return jdbcTemplate.queryForList("SELECT `release_id`, `release_num` FROM `release` " +
                        "WHERE `state` = " + ReleaseState.Final.getValue(),
                SimpleRelease.class);
    }

    public SimpleRelease getSimpleReleaseByReleaseId(long releaseId) {
        return jdbcTemplate.queryForObject("SELECT release_id, release_num " +
                        "FROM `release` WHERE release_id = :release_id",
                newSqlParameterSource().addValue("release_id", releaseId),
                SimpleRelease.class);
    }

    public List<ReleaseList> getReleaseList(User user) {
        List<ReleaseList> releaseLists =
                jdbcTemplate.queryForList("SELECT r.`release_id`, r.`release_num`, r.`state` as raw_state, " +
                        "n.`uri` as namespace, u.`login_id` as last_updated_by, r.`last_update_timestamp` " +
                        "FROM `release` r " +
                        "JOIN `namespace` n ON r.`namespace_id` = n.`namespace_id` " +
                        "JOIN `app_user` u ON r.`last_updated_by` = u.`app_user_id`", ReleaseList.class);

        releaseLists.forEach(releaseList -> {
            releaseList.setState(ReleaseState.valueOf(releaseList.getRawState()).name());
        });
        return releaseLists;
    }
}
