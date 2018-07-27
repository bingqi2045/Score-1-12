package org.oagi.srt.gateway.http.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SessionService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public long userId(User user) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("login_id", user.getUsername());
        return jdbcTemplate.queryForObject("SELECT app_user_id FROM app_user WHERE login_id = :login_id",
                parameterSource, Long.class);
    }
}
