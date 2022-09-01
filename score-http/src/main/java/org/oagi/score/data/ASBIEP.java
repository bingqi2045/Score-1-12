package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class ASBIEP implements BIE {

    private BigInteger asbiepId = BigInteger.ZERO;
    private String guid;
    private BigInteger basedAsccpManifestId = BigInteger.ZERO;
    private BigInteger roleOfAbieId = BigInteger.ZERO;
    private String definition;
    private String remark;
    private String bizTerm;
    private String createdBy;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private BigInteger ownerTopLevelAsbiepId = BigInteger.ZERO;
}
