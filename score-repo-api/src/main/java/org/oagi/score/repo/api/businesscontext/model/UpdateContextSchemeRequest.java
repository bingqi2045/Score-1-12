package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;

public class UpdateContextSchemeRequest extends Request {

    private String contextSchemeId;

    private String contextCategoryId;

    private String codeListId;

    private String schemeId;

    private String schemeName;

    private String description;

    private String schemeAgencyId;

    private String schemeVersionId;

    private Collection<ContextSchemeValue> contextSchemeValueList;

    public UpdateContextSchemeRequest(ScoreUser requester) {
        super(requester);
    }

    public String getContextSchemeId() {
        return contextSchemeId;
    }

    public void setContextSchemeId(String contextSchemeId) {
        this.contextSchemeId = contextSchemeId;
    }

    public UpdateContextSchemeRequest withContextSchemeId(String contextSchemeId) {
        this.setContextSchemeId(contextSchemeId);
        return this;
    }

    public String getContextCategoryId() {
        return contextCategoryId;
    }

    public void setContextCategoryId(String contextCategoryId) {
        this.contextCategoryId = contextCategoryId;
    }

    public UpdateContextSchemeRequest withContextCategoryId(String contextCategoryId) {
        this.setContextCategoryId(contextCategoryId);
        return this;
    }

    public String getCodeListId() {
        return codeListId;
    }

    public void setCodeListId(String codeListId) {
        this.codeListId = codeListId;
    }

    public UpdateContextSchemeRequest withCodeListId(String codeListId) {
        this.setCodeListId(codeListId);
        return this;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public UpdateContextSchemeRequest withSchemeId(String schemeId) {
        this.setSchemeId(schemeId);
        return this;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public UpdateContextSchemeRequest withSchemeName(String schemeName) {
        this.setSchemeName(schemeName);
        return this;
    }

    public String getSchemeAgencyId() {
        return schemeAgencyId;
    }

    public void setSchemeAgencyId(String schemeAgencyId) {
        this.schemeAgencyId = schemeAgencyId;
    }

    public UpdateContextSchemeRequest withSchemeAgencyId(String schemeAgencyId) {
        this.setSchemeAgencyId(schemeAgencyId);
        return this;
    }

    public String getSchemeVersionId() {
        return schemeVersionId;
    }

    public void setSchemeVersionId(String schemeVersionId) {
        this.schemeVersionId = schemeVersionId;
    }

    public UpdateContextSchemeRequest withSchemeVersionId(String schemeVersionId) {
        this.setSchemeVersionId(schemeVersionId);
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UpdateContextSchemeRequest withDescription(String description) {
        this.setDescription(description);
        return this;
    }

    public Collection<ContextSchemeValue> getContextSchemeValueList() {
        return (contextSchemeValueList == null) ? Collections.emptyList() : contextSchemeValueList;
    }

    public void setContextSchemeValueList(Collection<ContextSchemeValue> contextSchemeValueList) {
        this.contextSchemeValueList = contextSchemeValueList;
    }

    public UpdateContextSchemeRequest withContextSchemeValueList(Collection<ContextSchemeValue> contextSchemeValueList) {
        this.setContextSchemeValueList(contextSchemeValueList);
        return this;
    }
}
