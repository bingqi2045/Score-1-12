package org.oagi.score.gateway.http.api.release_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class SimpleRelease {

    private String releaseId;
    private String releaseNum;
    private ReleaseState state;
    private Date lastUpdateTimestamp;

}
