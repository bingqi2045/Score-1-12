package org.oagi.srt.gateway.http.api.common.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class Trackable implements Serializable {

    private int revisionNum;
    private int revisionTrackingNum;
    private Integer revisionAction;
    private Long releaseId;

}
