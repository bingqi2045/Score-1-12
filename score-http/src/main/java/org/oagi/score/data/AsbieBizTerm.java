package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class AsbieBizTerm {

    private BigInteger asbieBiztermId = BigInteger.ZERO;
    private BigInteger asccBiztermId = BigInteger.ZERO;
    private String asbieId;
    private String primaryIndicator;
    private String typeCode;
    private String businessTermId;
    private String businessTerm;
    private String guid;
    private String externalRefUri;
    private String createdBy;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;

}
