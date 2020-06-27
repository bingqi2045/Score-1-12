package org.oagi.srt.gateway.http.api;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.User;

public class DataAccessForbiddenException extends DataAccessException {

    public DataAccessForbiddenException(String msg) {
        super(msg);
    }

    public DataAccessForbiddenException(User user) {
        super("'" + user.getUsername() + "' doesn't have an access privilege.");
    }

}
