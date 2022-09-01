package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Response;

import java.math.BigInteger;

public class UpdateContextSchemeResponse extends Response {

    private final String contextSchemeId;
    private final boolean changed;

    public UpdateContextSchemeResponse(String contextSchemeId, boolean changed) {
        this.contextSchemeId = contextSchemeId;
        this.changed = changed;
    }

    public String getContextSchemeId() {
        return contextSchemeId;
    }

    public boolean isChanged() {
        return changed;
    }
}
