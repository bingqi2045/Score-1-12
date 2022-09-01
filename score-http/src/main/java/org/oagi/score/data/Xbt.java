package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class Xbt {

    private String xbtId;
    private BigInteger manifestId = BigInteger.ZERO;
    private String name;
    private String builtinType;
    private String jbtDraft05Map;
    private String openapi30Map;
    private BigInteger subtypeOfXbtId = BigInteger.ZERO;
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
