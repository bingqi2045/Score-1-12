package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Auditable;

import java.math.BigInteger;
import java.util.Collection;

public class ContextScheme extends Auditable {

    private BigInteger contextSchemeId;

    private String guid;

    private String schemeId;

    private String schemeName;

    private String description;

    private String schemeAgencyId;

    private String schemeVersionId;

    private BigInteger contextCategoryId;

    private boolean imported;

    private Collection<ContextSchemeValue> contextSchemeValues;

    public BigInteger getContextSchemeId() {
        return contextSchemeId;
    }

    public void setContextSchemeId(BigInteger contextSchemeId) {
        this.contextSchemeId = contextSchemeId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public Collection<ContextSchemeValue> getContextSchemeValues() {
        return contextSchemeValues;
    }

    public void setContextSchemeValues(Collection<ContextSchemeValue> contextSchemeValues) {
        this.contextSchemeValues = contextSchemeValues;
    }
}
