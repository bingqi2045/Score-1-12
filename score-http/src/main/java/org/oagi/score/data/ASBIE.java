package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class ASBIE implements BIE {

    private String asbieId;
    private String guid;
    private String fromAbieId;
    private String toAsbiepId;
    private BigInteger basedAsccManifestId = BigInteger.ZERO;
    private String definition;
    private int cardinalityMin;
    private int cardinalityMax;
    private boolean nillable;
    private String remark;
    private String createdBy;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private double seqKey;
    private boolean used;
    private String ownerTopLevelAsbiepId;
}
