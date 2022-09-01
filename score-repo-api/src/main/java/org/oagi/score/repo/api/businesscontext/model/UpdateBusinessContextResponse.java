package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Response;

import java.math.BigInteger;

public class UpdateBusinessContextResponse extends Response {

    private final String businessContextId;
    private final boolean changed;

    public UpdateBusinessContextResponse(String businessContextId, boolean changed) {
        this.businessContextId = businessContextId;
        this.changed = changed;
    }

    public String getBusinessContextId() {
        return businessContextId;
    }

    public boolean isChanged() {
        return changed;
    }
}
