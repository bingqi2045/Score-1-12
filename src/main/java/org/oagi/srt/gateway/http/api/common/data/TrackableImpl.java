package org.oagi.srt.gateway.http.api.common.data;

import lombok.Data;
import org.oagi.srt.data.Trackable;

@Data
public abstract class TrackableImpl implements Trackable {

    private long releaseId;
    private String releaseNum;

    private long revisionId;
    private int revisionNum;
    private int revisionTrackingNum;

}
