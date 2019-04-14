package org.oagi.srt.gateway.http.configuration.security;

import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.entity.jooq.tables.records.AppUserRecord;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

public class AppUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final boolean oagisDeveloper;

    public AppUserDetails(AppUserRecord appUserRecord) {
        this.username = appUserRecord.get(Tables.APP_USER.LOGIN_ID);
        this.password = appUserRecord.get(Tables.APP_USER.PASSWORD);
        this.oagisDeveloper = (appUserRecord.getOagisDeveloperIndicator() == 1) ? true : false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority((this.oagisDeveloper) ? "developer" : "end-user"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
