package org.oagi.srt.gateway.http.context_management;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ContextScheme {
    private long ctxSchemeId;
    private String guid;
    private String schemeName;
    private long ctxCategoryId;
    private String ctxCategoryName;
    private String schemeId;
    private String schemeAgencyId;
    private String schemeVersionId;
    private String description;
    private Date lastUpdateTimestamp;
    private List<ContextSchemeValue> ctxSchemeValues;

    public long getCtxSchemeId() {
        return ctxSchemeId;
    }

    public void setCtxSchemeId(long ctxSchemeId) {
        this.ctxSchemeId = ctxSchemeId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(Date lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public List<ContextSchemeValue> getCtxSchemeValues() {
        return (ctxSchemeValues != null) ? ctxSchemeValues : Collections.emptyList();
    }

    public void setCtxSchemeValues(List<ContextSchemeValue> ctxSchemeValues) {
        this.ctxSchemeValues = ctxSchemeValues;
    }
}
