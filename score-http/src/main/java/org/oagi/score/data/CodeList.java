package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class CodeList {

    private String codeListId;
    private String guid;
    private String enumTypeGuid;
    private String name;
    private String listId;
    private String agencyIdListValueId;
    private String versionId;
    private String definition;
    private String remark;
    private String definitionSource;
    private String basedCodeListId;
    private boolean extensibleIndicator;
    private String moduleId;
    private String createdBy;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private String state;
}
