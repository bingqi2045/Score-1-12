package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class CodeList {

    private BigInteger codeListId = BigInteger.ZERO;
    private String guid;
    private String enumTypeGuid;
    private String name;
    private String listId;
    private BigInteger agencyId = BigInteger.ZERO;
    private String versionId;
    private String definition;
    private String remark;
    private String definitionSource;
    private BigInteger basedCodeListId = BigInteger.ZERO;
    private boolean extensibleIndicator;
    private BigInteger moduleId = BigInteger.ZERO;
    private String createdBy;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private String state;
}
