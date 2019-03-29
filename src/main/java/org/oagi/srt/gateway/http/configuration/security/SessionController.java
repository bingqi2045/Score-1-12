package org.oagi.srt.gateway.http.configuration.security;

import org.oagi.srt.data.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SessionController {

    @Autowired
    private FindByIndexNameSessionRepository sessionRepository;

    @Autowired
    private RememberMeServices rememberMeServices;

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void login() {
    }

    @RequestMapping(value = "/users/me", method = RequestMethod.GET)
    public Map<String, String> userInfo(@AuthenticationPrincipal User user) {
        if (user == null) {
            throw new BadCredentialsException("Invalid Credentials");
        }

        AppUser appUser = sessionService.getAppUser(user.getUsername());

        Map<String, String> resp = new HashMap();
        resp.put("username", user.getUsername());
        resp.put("role", (appUser.isOagisDeveloper()) ? "developer" : "end-user");
        return resp;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal User user, HttpSession session, HttpServletRequest request) {
        sessionRepository.deleteById(session.getId());
        rememberMeServices.loginFail(request, null);
        session.invalidate();
    }
}
