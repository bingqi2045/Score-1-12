package org.oagi.srt.gateway.http.configuration.security;

import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class SessionService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    public long userId(User user) {
        return jdbcTemplate.queryForObject("SELECT app_user_id FROM app_user WHERE login_id = :login_id",
                newSqlParameterSource().addValue("login_id", user.getUsername()), Long.class);
    }
}
