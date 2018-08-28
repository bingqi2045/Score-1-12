package org.oagi.srt.data;

import java.io.Serializable;

public interface Trackable extends Serializable {

    public Long getReleaseId();

    public int getRevisionNum();

    public int getRevisionTrackingNum();

}
