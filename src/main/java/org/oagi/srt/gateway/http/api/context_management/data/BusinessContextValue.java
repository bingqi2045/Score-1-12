package org.oagi.srt.gateway.http.api.context_management.data;

import lombok.Data;

@Data
public class BusinessContextValue {

    private long bizCtxValueId;
    private String guid;
    private long ctxCategoryId;
    private String ctxCategoryName;
    private long ctxSchemeId;
    private String ctxSchemeName;
    private long ctxSchemeValueId;
    private String ctxSchemeValue;

}
