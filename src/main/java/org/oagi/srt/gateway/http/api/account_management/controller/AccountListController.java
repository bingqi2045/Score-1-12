package org.oagi.srt.gateway.http.api.account_management.controller;

import org.oagi.srt.gateway.http.api.account_management.data.AppUser;
import org.oagi.srt.gateway.http.api.account_management.service.AccountListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AccountListController {

    @Autowired
    private AccountListService service;

    @RequestMapping(value = "/accounts_list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<AppUser> getAccounts() {
        return service.getAccounts();
    }

    @RequestMapping(value = "/account/{loginId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public AppUser getAccount(@PathVariable("loginId") String loginId) {
        return service.getAccount(loginId);
    }

    @RequestMapping(value = "/account", method = RequestMethod.PUT)
    public ResponseEntity create(@RequestBody AppUser account) {
        service.insert(account);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/accounts/names", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<String> getAccountLoginIds() {
        return service.getAccountLoginIds();
    }

    @RequestMapping(value = "/account/_check/loginId/hasTaken", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Boolean hasTaken(@RequestBody Map<String, String> body) {
        return service.hasTaken(body.getOrDefault("loginId", ""));
    }

}
