package org.oagi.srt.data;

import java.io.Serializable;

public interface Trackable extends Serializable {

    long getId();

    long getReleaseId();
    String getReleaseNum();

    long getRevisionId();
    int getRevisionNum();
    int getRevisionTrackingNum();


}
