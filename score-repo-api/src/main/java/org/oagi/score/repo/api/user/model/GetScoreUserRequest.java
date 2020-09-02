package org.oagi.score.repo.api.user.model;

import org.oagi.score.repo.api.base.Request;

import java.math.BigInteger;

public class GetScoreUserRequest extends Request {

    private BigInteger userId;

    private String username;

    public GetScoreUserRequest() {
        super();
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public GetScoreUserRequest withUserId(BigInteger userId) {
        setUserId(userId);
        return this;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GetScoreUserRequest withUserName(String username) {
        setUsername(username);
        return this;
    }
}
