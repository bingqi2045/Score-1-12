package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class BbieBizTerm {

    private String bbieBiztermId;
    private String bccBiztermId;
    private String bbieId;
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
