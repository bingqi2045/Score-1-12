package org.oagi.srt.gateway.http.context_management;

public class SimpleContextScheme {
    private long ctxSchemeId;
    private String schemeName;
    private String schemeId;
    private String schemeAgencyId;
    private String schemeVersionId;

    public long getCtxSchemeId() {
        return ctxSchemeId;
    }

    public void setCtxSchemeId(long ctxSchemeId) {
        this.ctxSchemeId = ctxSchemeId;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
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
}
