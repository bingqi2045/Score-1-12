package org.oagi.score.service.common.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public abstract class TrackableImpl<T> implements Trackable<T> {

    private String releaseId;
    private String releaseNum;

    private String logId;
    private int revisionNum;
    private int revisionTrackingNum;

}
