package org.oagi.score.gateway.http.api.common.data;

import lombok.Data;
import org.oagi.score.data.Trackable;

import java.math.BigInteger;

@Data
public abstract class TrackableImpl implements Trackable {

    private BigInteger releaseId;
    private String releaseNum;

    private BigInteger revisionId;
    private int revisionNum;
    private int revisionTrackingNum;

}
