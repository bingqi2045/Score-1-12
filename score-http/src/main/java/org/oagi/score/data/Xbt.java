package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class Xbt {

    private String xbtId;
    private String manifestId;
    private String name;
    private String builtinType;
    private String jbtDraft05Map;
    private String openapi30Map;
    private String subtypeOfXbtId;
    private String schemaDefinition;
    private String releaseId;
    private Integer state;
    private String createdBy;
    private String ownerUserId;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private Boolean deprecated;

}
