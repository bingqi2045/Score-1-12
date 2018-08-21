package org.oagi.srt.gateway.http.api.common.data;

import lombok.Data;

@Data
public class TrackableImpl implements Trackable {

    private Long releaseId;
    private int revisionNum;
    private int revisionTrackingNum;

}
