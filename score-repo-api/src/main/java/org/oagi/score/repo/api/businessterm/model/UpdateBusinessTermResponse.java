package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Response;

import java.math.BigInteger;

public class UpdateBusinessTermResponse extends Response {

    private final String businessTermId;
    private final boolean changed;

    public UpdateBusinessTermResponse(String businessTermId, boolean changed) {
        this.businessTermId = businessTermId;
        this.changed = changed;
    }

    public String getBusinessTermId() {
        return businessTermId;
    }

    public boolean isChanged() {
        return changed;
    }
}
