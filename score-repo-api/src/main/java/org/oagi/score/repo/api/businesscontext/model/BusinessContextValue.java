package org.oagi.score.repo.api.businesscontext.model;

import java.io.Serializable;

public class BusinessContextValue implements Serializable {

    private String businessContextValueId;
    private String businessContextId;

    private String contextCategoryId;
    private String contextCategoryName;

    private String contextSchemeId;
    private String contextSchemeName;

    private String contextSchemeValueId;
    private String contextSchemeValue;
    private String contextSchemeValueMeaning;

    public String getBusinessContextValueId() {
        return businessContextValueId;
    }

    public void setBusinessContextValueId(String businessContextValueId) {
        this.businessContextValueId = businessContextValueId;
    }

    public String getBusinessContextId() {
        return businessContextId;
    }

    public void setBusinessContextId(String businessContextId) {
        this.businessContextId = businessContextId;
    }

    public String getContextCategoryId() {
        return contextCategoryId;
    }

    public void setContextCategoryId(String contextCategoryId) {
        this.contextCategoryId = contextCategoryId;
    }

    public String getContextCategoryName() {
        return contextCategoryName;
    }

    public void setContextCategoryName(String contextCategoryName) {
        this.contextCategoryName = contextCategoryName;
    }

    public String getContextSchemeId() {
        return contextSchemeId;
    }

    public void setContextSchemeId(String contextSchemeId) {
        this.contextSchemeId = contextSchemeId;
    }

    public String getContextSchemeName() {
        return contextSchemeName;
    }

    public void setContextSchemeName(String contextSchemeName) {
        this.contextSchemeName = contextSchemeName;
    }

    public String getContextSchemeValueId() {
        return contextSchemeValueId;
    }

    public void setContextSchemeValueId(String contextSchemeValueId) {
        this.contextSchemeValueId = contextSchemeValueId;
    }

    public String getContextSchemeValue() {
        return contextSchemeValue;
    }

    public void setContextSchemeValue(String contextSchemeValue) {
        this.contextSchemeValue = contextSchemeValue;
    }

    public String getContextSchemeValueMeaning() {
        return contextSchemeValueMeaning;
    }

    public void setContextSchemeValueMeaning(String contextSchemeValueMeaning) {
        this.contextSchemeValueMeaning = contextSchemeValueMeaning;
    }
}
