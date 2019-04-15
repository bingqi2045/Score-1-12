package org.oagi.srt.gateway.http.configuration.security;

import org.jooq.DSLContext;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.entity.jooq.tables.records.AppUserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private DSLContext dslContext;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUserRecord appUserRecord = dslContext.selectFrom(Tables.APP_USER)
                .where(Tables.APP_USER.LOGIN_ID.eq(username))
                .fetchOptional().orElse(null);
        if (appUserRecord == null) {
            throw new UsernameNotFoundException(username);
        }

        String password = appUserRecord.get(Tables.APP_USER.PASSWORD);
        boolean oagisDeveloper = (appUserRecord.getOagisDeveloperIndicator() == 1) ? true : false;

        return new User(username, password,
                Arrays.asList(new SimpleGrantedAuthority((oagisDeveloper) ? "developer" : "end-user")));
    }
}
