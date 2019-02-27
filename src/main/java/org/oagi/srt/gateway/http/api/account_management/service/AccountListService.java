package org.oagi.srt.gateway.http.api.account_management.service;

import org.jooq.DSLContext;
import org.oagi.srt.gateway.http.api.account_management.data.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.oagi.srt.entity.jooq.Tables.APP_USER;

@Service
@Transactional(readOnly = true)
public class AccountListService {

    @Autowired
    private DSLContext dslContext;

    public List<AppUser> getAccounts(){
        return dslContext.select(
                APP_USER.APP_USER_ID,
                APP_USER.LOGIN_ID,
                APP_USER.NAME,
                APP_USER.OAGIS_DEVELOPER_INDICATOR,
                APP_USER.ORGANIZATION
        ).from(APP_USER)
                .fetchInto(AppUser.class);
    }
}
