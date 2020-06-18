package org.oagi.srt.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class TopLevelAbie {

    private BigInteger topLevelAbieId = BigInteger.ZERO;
    private BigInteger abieId = BigInteger.ZERO;
    private BigInteger ownerUserId = BigInteger.ZERO;
    private BigInteger releaseId = BigInteger.ZERO;
    private BieState state;
    private BigInteger lastUpdatedBy = BigInteger.ZERO;
    private Date lastUpdateTimestamp;

}
