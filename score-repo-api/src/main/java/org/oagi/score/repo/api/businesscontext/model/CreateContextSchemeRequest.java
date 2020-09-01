package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.base.ScoreUser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CreateContextSchemeRequest extends Request {

    private String schemeId;

    private String schemeName;

    private String description;

    private String schemeAgencyId;

    private String schemeVersionId;

    private BigInteger contextCategoryId;

    private Collection<ContextSchemeValue> contextSchemeValues;

    public CreateContextSchemeRequest(ScoreUser requester) {
        super(requester);
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSchemeAgencyId() {
        return schemeAgencyId;
    }

    public void setSchemeAgencyId(String schemeAgencyId) {
        this.schemeAgencyId = schemeAgencyId;
    }

    public String getSchemeVersionId() {
        return schemeVersionId;
    }

    public void setSchemeVersionId(String schemeVersionId) {
        this.schemeVersionId = schemeVersionId;
    }

    public BigInteger getContextCategoryId() {
        return contextCategoryId;
    }

    public void setContextCategoryId(BigInteger contextCategoryId) {
        this.contextCategoryId = contextCategoryId;
    }

    public Collection<ContextSchemeValue> getContextSchemeValues() {
        return (contextSchemeValues == null) ? Collections.emptyList() : contextSchemeValues;
    }

    public void addContextSchemeValue(String value, String meaning) {
        if (value != null) {
            if (this.contextSchemeValues == null) {
                this.contextSchemeValues = new ArrayList();
            }

            ContextSchemeValue contextSchemeValue = new ContextSchemeValue();
            contextSchemeValue.setValue(value);
            contextSchemeValue.setMeaning(meaning);
            this.contextSchemeValues.add(contextSchemeValue);
        }
    }
}