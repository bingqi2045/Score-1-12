package org.oagi.score.data;

import lombok.Data;
import org.oagi.score.repo.api.bie.model.BieState;

import java.math.BigInteger;
import java.util.Date;

@Data
public class TopLevelAsbiep {

    private BigInteger topLevelAsbiepId = BigInteger.ZERO;
    private BigInteger asbiepId = BigInteger.ZERO;
    private String ownerUserId;
    private String releaseId;
    private String version;
    private String status;
    private BieState state;
    private String lastUpdatedBy;
    private Date lastUpdateTimestamp;

}
