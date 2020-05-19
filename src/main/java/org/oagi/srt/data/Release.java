package org.oagi.srt.data;

import lombok.Data;
import org.oagi.srt.entity.jooq.enums.ReleaseState;

import java.math.BigInteger;
import java.util.Date;

@Data
public class Release {

    private BigInteger releaseId = BigInteger.ZERO;
    private String releaseNum;
    private String releaseNote;
    private BigInteger namespaceId = BigInteger.ZERO;
    private BigInteger createdBy = BigInteger.ZERO;
    private BigInteger lastUpdatedBy = BigInteger.ZERO;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private ReleaseState state;

}
