package org.oagi.srt.gateway.http.api.account_management.service;

import org.jooq.DSLContext;
import org.oagi.srt.entity.jooq.tables.records.AppUserRecord;
import org.oagi.srt.gateway.http.api.account_management.data.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.oagi.srt.entity.jooq.Tables.APP_USER;

@Service
@Transactional(readOnly = true)
public class AccountListService {

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        AppUserRecord record = new AppUserRecord();
        record.setLoginId(account.getLoginId());
        record.setPassword(passwordEncoder.encode(account.getPassword()));
        record.setName(account.getName());
        record.setOrganization(account.getOrganization());
        record.setOagisDeveloperIndicator((byte) (account.isOagisDeveloperIndicator() ? 1 : 0));

        dslContext.insertInto(APP_USER)
                .set(record)
                .execute();
    }

    public boolean hasTaken(String loginId) {
        return dslContext.select(APP_USER.APP_USER_ID)
                .from(APP_USER)
                .where(APP_USER.LOGIN_ID.eq(loginId))
                .fetchOptionalInto(Long.class).orElse(0L) != 0L;
    }
}
