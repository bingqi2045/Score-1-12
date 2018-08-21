package org.oagi.srt.gateway.http.api.user_management;

import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {

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
}
