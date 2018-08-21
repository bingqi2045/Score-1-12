package org.oagi.srt.gateway.http.api.common.data;

import java.io.Serializable;

public interface Trackable extends Serializable {

    public Long getReleaseId();

    public int getRevisionNum();

    public int getRevisionTrackingNum();

}
