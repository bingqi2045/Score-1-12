package org.oagi.srt.data;

import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;

public class RepositoryRequest {

    private final User user;
    private final LocalDateTime localDateTime;

    public RepositoryRequest(User user) {
        this(user, LocalDateTime.now());
    }

    public RepositoryRequest(User user,
                             LocalDateTime localDateTime) {
        this.user = user;
        this.localDateTime = localDateTime;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
