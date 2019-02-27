package org.oagi.srt.gateway.http.api.account_management.controller;

import org.oagi.srt.gateway.http.api.account_management.data.AppUser;
import org.oagi.srt.gateway.http.api.account_management.service.AccountListService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountListController {

    @Autowired
    private AccountListService service;

    @RequestMapping(value = "/accounts_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<AppUser> getAccounts(){return service.getAccounts();}

}
