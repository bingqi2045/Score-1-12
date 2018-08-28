package org.oagi.srt.repository;

import org.oagi.srt.data.AppUser;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class UserRepository implements SrtRepository<AppUser> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    public Map<Long, String> getUsernameMap() {
        return jdbcTemplate.query("SELECT app_user_id, login_id FROM app_user", rse -> {
            Map<Long, String> usernameMap = new HashMap();
            while (rse.next()) {
                usernameMap.put(
                        rse.getLong("app_user_id"),
                        rse.getString("login_id"));
            }
            return usernameMap;
        });
    }

    private String GET_APP_USER_STATEMENT = "SELECT `app_user_id`,`login_id`,`password`,`name`,`organization`," +
            "`oagis_developer_indicator` as oagis_developer FROM `app_user`";

    @Override
    public List<AppUser> findAll() {
        return jdbcTemplate.queryForList(GET_APP_USER_STATEMENT, AppUser.class);
    }

    @Override
    public AppUser findById(long id) {
        return jdbcTemplate.queryForObject(new StringBuilder(GET_APP_USER_STATEMENT)
                .append(" WHERE `app_user_id` = :id").toString(), newSqlParameterSource()
                .addValue("id", id), AppUser.class);
    }
}
