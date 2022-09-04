package org.oagi.score.gateway.http.api;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;

public class DataAccessForbiddenException extends DataAccessException {

    private String errorMessageId;

    public DataAccessForbiddenException(String msg) {
        super(msg);
    }

    public DataAccessForbiddenException(String msg, String errorMessageId) {
        super(msg);
        this.errorMessageId = errorMessageId;
    }

    public DataAccessForbiddenException(AuthenticatedPrincipal user) {
        super("'" + user.getName() + "' doesn't have an access privilege.");
    }

    public String getErrorMessageId() {
        return errorMessageId;
    }
}
