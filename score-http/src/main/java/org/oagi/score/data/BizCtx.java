package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class BizCtx {

    private BigInteger bizCtxId = BigInteger.ZERO;
    private String guid;
    private String name;
    private String createdBy;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
}
