package org.oagi.srt.data;

import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;

public class RepositoryRequest {

    private final AuthenticatedPrincipal user;
    private final LocalDateTime localDateTime;

    public RepositoryRequest(AuthenticatedPrincipal user) {
        this(user, LocalDateTime.now());
    }

    public RepositoryRequest(AuthenticatedPrincipal user,
                             LocalDateTime localDateTime) {
        this.user = user;
        this.localDateTime = localDateTime;
    }

    public AuthenticatedPrincipal getUser() {
        return user;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
