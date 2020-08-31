package org.oagi.score.repo.api.base;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public final class ScoreUser implements Serializable {

    private BigInteger userId;

    private String username;

    private Collection<ScoreRole> roles;

    public ScoreUser(BigInteger userId, String username, ScoreRole role) {
        this(userId, username, Arrays.asList(role));
    }

    public ScoreUser(BigInteger userId, String username, Collection<ScoreRole> roles) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Collection<ScoreRole> getRoles() {
        return (roles == null) ? Collections.emptyList() : roles;
    }

    public boolean hasRole(ScoreRole role) {
        if (role == null) {
            throw new IllegalArgumentException();
        }

        return hasAnyRole(Arrays.asList(role));
    }

    public boolean hasAnyRole(ScoreRole... roles) {
        if (roles == null || roles.length == 0) {
            throw new IllegalArgumentException();
        }

        return hasAnyRole(Arrays.asList(roles));
    }

    public boolean hasAnyRole(Collection<ScoreRole> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (this.roles == null || this.roles.isEmpty()) {
            return false;
        }

        for (ScoreRole role : roles) {
            return this.roles.contains(role);
        }

        return false;
    }

    private void ensureRolesInstantiated() {
        if (this.roles == null || this.roles.isEmpty()) {
            this.roles = new ArrayList();
        }
    }

    public void addRole(ScoreRole role) {
        if (role == null) {
            return;
        }

        ensureRolesInstantiated();
        this.roles.add(role);
    }

    public void addRoles(Collection<ScoreRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return;
        }

        ensureRolesInstantiated();
        this.roles.addAll(roles);
    }

    public void setRoles(Collection<ScoreRole> roles) {
        this.roles = roles;
    }
}
