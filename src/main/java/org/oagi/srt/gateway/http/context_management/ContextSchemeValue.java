package org.oagi.srt.gateway.http.context_management;

public class ContextSchemeValue {

    private long ctxSchemeValueId;
    private String guid;
    private String value;
    private String meaning;
    private long ownerCtxSchemeId;

    public long getCtxSchemeValueId() {
        return ctxSchemeValueId;
    }

    public void setCtxSchemeValueId(long ctxSchemeValueId) {
        this.ctxSchemeValueId = ctxSchemeValueId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public long getOwnerCtxSchemeId() {
        return ownerCtxSchemeId;
    }

    public void setOwnerCtxSchemeId(long ownerCtxSchemeId) {
        this.ownerCtxSchemeId = ownerCtxSchemeId;
    }
}
