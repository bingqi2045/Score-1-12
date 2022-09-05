package org.oagi.score.repo.api.user.model;

import org.oagi.score.repo.api.base.Request;

public class GetScoreUserRequest extends Request {

    private String userId;

    private String username;

    private String oidcSub;

    public GetScoreUserRequest() {
        super();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public GetScoreUserRequest withUserId(String userId) {
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

    public String getOidcSub() {
        return oidcSub;
    }

    public void setOidcSub(String oidcSub) {
        this.oidcSub = oidcSub;
    }

    public GetScoreUserRequest withOidcSub(String oidcSub) {
        this.setOidcSub(oidcSub);
        return this;
    }
}
