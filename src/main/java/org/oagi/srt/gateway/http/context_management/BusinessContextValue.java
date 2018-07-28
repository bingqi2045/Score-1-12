package org.oagi.srt.gateway.http.context_management;

public class BusinessContextValue {
    private long bizCtxValueId;
    private String guid;
    private long ctxCategoryId;
    private String ctxCategoryName;
    private long ctxSchemeId;
    private String ctxSchemeName;
    private long ctxSchemeValueId;
    private String ctxSchemeValue;

    public long getBizCtxValueId() {
        return bizCtxValueId;
    }

    public void setBizCtxValueId(long bizCtxValueId) {
        this.bizCtxValueId = bizCtxValueId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public long getCtxCategoryId() {
        return ctxCategoryId;
    }

    public void setCtxCategoryId(long ctxCategoryId) {
        this.ctxCategoryId = ctxCategoryId;
    }

    public String getCtxCategoryName() {
        return ctxCategoryName;
    }

    public void setCtxCategoryName(String ctxCategoryName) {
        this.ctxCategoryName = ctxCategoryName;
    }

    public long getCtxSchemeId() {
        return ctxSchemeId;
    }

    public void setCtxSchemeId(long ctxSchemeId) {
        this.ctxSchemeId = ctxSchemeId;
    }

    public String getCtxSchemeName() {
        return ctxSchemeName;
    }

    public void setCtxSchemeName(String ctxSchemeName) {
        this.ctxSchemeName = ctxSchemeName;
    }

    public long getCtxSchemeValueId() {
        return ctxSchemeValueId;
    }

    public void setCtxSchemeValueId(long ctxSchemeValueId) {
        this.ctxSchemeValueId = ctxSchemeValueId;
    }

    public String getCtxSchemeValue() {
        return ctxSchemeValue;
    }

    public void setCtxSchemeValue(String ctxSchemeValue) {
        this.ctxSchemeValue = ctxSchemeValue;
    }
}
