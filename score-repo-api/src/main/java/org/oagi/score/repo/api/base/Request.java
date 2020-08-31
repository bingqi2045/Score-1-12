package org.oagi.score.repo.api.base;

import java.io.Serializable;

public class Request implements Serializable {

    private final ScoreUser requester;

    public Request(ScoreUser requester) {
        if (requester == null) {
            throw new IllegalArgumentException();
        }

        this.requester = requester;
    }

    public final ScoreUser getRequester() {
        return this.requester;
    }

}
