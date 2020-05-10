package org.oagi.srt.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class Xbt {

    private BigInteger xbtId = BigInteger.ZERO;
    private String name;
    private String builtinType;
    private String jbtDraft05Map;
    private String openapi30Map;
    private BigInteger subtypeOfXbtId = BigInteger.ZERO;
    private String schemaDefinition;
    private BigInteger moduleId = BigInteger.ZERO;
    private BigInteger releaseId = BigInteger.ZERO;
    private String revisionDoc;
    private Integer state;
    private BigInteger createdBy = BigInteger.ZERO;
    private BigInteger ownerUserId = BigInteger.ZERO;
    private BigInteger lastUpdatedBy = BigInteger.ZERO;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private BigInteger revisionId = BigInteger.ZERO;
    private BigInteger currentXbtId = BigInteger.ZERO;
    private Boolean deprecated;

}
