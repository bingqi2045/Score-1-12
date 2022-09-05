package org.oagi.score.data;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ContextScheme implements Serializable {

    private String ctxSchemeId;
    private String guid;
    private String schemeId;
    private String schemeName;
    private String description;
    private String schemeAgencyId;
    private String schemeVersionId;
    private String ctxCategoryId;
    private String createdBy;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
}
