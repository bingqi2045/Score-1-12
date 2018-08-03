package org.oagi.srt.gateway.http.api.context_management.data;

import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
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
    private List<ContextSchemeValue> ctxSchemeValues = Collections.emptyList();

}
