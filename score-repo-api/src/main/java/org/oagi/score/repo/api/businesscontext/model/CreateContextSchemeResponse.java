package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Response;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CreateContextSchemeResponse extends Response {

    private final String contextSchemeId;

    private Collection<ContextSchemeValue> contextSchemeValues;

    public CreateContextSchemeResponse(String contextSchemeId) {
        this.contextSchemeId = contextSchemeId;
    }

    public String getContextSchemeId() {
        return contextSchemeId;
    }

    public Collection<ContextSchemeValue> getContextSchemeValues() {
        return (contextSchemeValues == null) ? Collections.emptyList() : contextSchemeValues;
    }

    public void setContextSchemeValues(Collection<ContextSchemeValue> contextSchemeValues) {
        this.contextSchemeValues = contextSchemeValues;
    }

    public void addContextSchemeValue(ContextSchemeValue contextSchemeValue) {
        if (this.contextSchemeValues == null) {
            this.contextSchemeValues = new ArrayList();
        }

        this.contextSchemeValues.add(contextSchemeValue);
    }
}
