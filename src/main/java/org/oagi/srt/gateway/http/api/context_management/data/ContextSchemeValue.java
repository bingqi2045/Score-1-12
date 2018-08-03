package org.oagi.srt.gateway.http.api.context_management.data;

import lombok.Data;

@Data
public class ContextSchemeValue {

    private long ctxSchemeValueId;
    private String guid;
    private String value;
    private String meaning;
    private long ownerCtxSchemeId;

}
