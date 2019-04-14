package org.oagi.srt.gateway.http.configuration.security;

import org.jooq.DSLContext;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.entity.jooq.tables.records.AppUserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private DSLContext dslContext;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUserRecord appUserRecord = dslContext.selectFrom(Tables.APP_USER)
                .where(Tables.APP_USER.LOGIN_ID.eq(username))
                .fetchOptional().orElse(null);
        if (appUserRecord == null) {
            throw new UsernameNotFoundException(username);
        }

        return new AppUserDetails(appUserRecord);
    }
}
