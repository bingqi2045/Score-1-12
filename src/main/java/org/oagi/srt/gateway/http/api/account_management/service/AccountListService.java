package org.oagi.srt.gateway.http.api.account_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.account_management.data.AppUser;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.oagi.srt.entity.jooq.Tables.APP_USER;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class AccountListService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private DSLContext dslContext;

    public List<AppUser> getAccounts() {
        return dslContext.select(
                APP_USER.APP_USER_ID,
                APP_USER.LOGIN_ID,
                APP_USER.NAME,
                APP_USER.OAGIS_DEVELOPER_INDICATOR,
                APP_USER.ORGANIZATION
        ).from(APP_USER)
                .fetchInto(AppUser.class);
    }

    public AppUser getAccount(String loginId) {
        return dslContext.select(
                APP_USER.APP_USER_ID,
                APP_USER.LOGIN_ID,
                APP_USER.PASSWORD,
                APP_USER.NAME,
                APP_USER.OAGIS_DEVELOPER_INDICATOR,
                APP_USER.ORGANIZATION
        ).from(APP_USER).where(APP_USER.LOGIN_ID.eq(loginId))
                .fetchOneInto(AppUser.class);

    }

    @Transactional
    public void insert(AppUser account) {
        jdbcTemplate.insert()
                .withTableName("app_user")
                .usingColumns("login_id", "password" ,"name", "organization", "oagis_developer_indicator")
                .execute(newSqlParameterSource()
                        .addValue("login_id", account.getLoginId())
                        .addValue("password", passwordEncoder.encode(account.getPassword()))
                        .addValue("name", account.getName())
                        .addValue("organization", account.getOrganization())
                        .addValue("oagis_developer_indicator", account.isOagisDeveloperIndicator()));
    }
}
