package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class BbieBizterm {

    private BigInteger bbieBiztermId = BigInteger.ZERO;
    private BigInteger bccBiztermId = BigInteger.ZERO;
    private BigInteger bbieId = BigInteger.ZERO;
    private String primaryIndicator;
    private String typeCode;
    private BigInteger businessTermId = BigInteger.ZERO;
    private String businessTerm;
    private String guid;
    private String externalRefUri;
    private String createdBy;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;

}
