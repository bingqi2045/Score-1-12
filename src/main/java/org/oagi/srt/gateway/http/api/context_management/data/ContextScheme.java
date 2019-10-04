package org.oagi.srt.gateway.http.api.context_management.data;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class ContextScheme implements Serializable {

    private long ctxSchemeId;
    private String guid;
    private String schemeName;
    private long ctxCategoryId;
    private String ctxCategoryName;
    private String schemeId;
    private String schemeAgencyId;
    private String schemeVersionId;
    private long codeListId;
    private String codeListName;
    private String description;
    private Date lastUpdateTimestamp;
    private String lastUpdateUser;
    private List<ContextSchemeValue> ctxSchemeValues = Collections.emptyList();
    private boolean used;

}
