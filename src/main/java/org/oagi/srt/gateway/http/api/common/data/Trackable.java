package org.oagi.srt.gateway.http.api.common.data;

import lombok.Data;

@Data
public class Trackable {

    private int revisionNum;
    private int revisionTrackingNum;
    private int revisionAction;
    private long releaseId;

}
