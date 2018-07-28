package org.oagi.srt.gateway.http.context_management;

public class SimpleContextSchemeValue {
    private long ctxSchemeValueId;
    private String value;
    private String meaning;

    public long getCtxSchemeValueId() {
        return ctxSchemeValueId;
    }

    public void setCtxSchemeValueId(long ctxSchemeValueId) {
        this.ctxSchemeValueId = ctxSchemeValueId;
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
}
