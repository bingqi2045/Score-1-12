package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class AsbieBizterm {

    private BigInteger asbieBiztermId = BigInteger.ZERO;
    private BigInteger asccBiztermId = BigInteger.ZERO;
    private BigInteger asbieId = BigInteger.ZERO;
    private String primaryIndicator;
    private String typeCode;
    private BigInteger businessTermId = BigInteger.ZERO;
    private String businessTerm;
    private String guid;
    private String externalRefUri;
    private BigInteger createdBy = BigInteger.ZERO;
    private BigInteger lastUpdatedBy = BigInteger.ZERO;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;

}
