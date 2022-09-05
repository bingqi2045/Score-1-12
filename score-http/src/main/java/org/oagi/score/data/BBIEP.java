package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class BBIEP implements BIE {

    private String bbiepId;
    private String guid;
    private String basedBccpManifestId;
    private String definition;
    private String remark;
    private String bizTerm;
    private String createdBy;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private String ownerTopLevelAsbiepId;
}
