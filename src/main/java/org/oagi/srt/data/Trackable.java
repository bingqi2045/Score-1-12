package org.oagi.srt.data;

import java.io.Serializable;

public interface Trackable extends Serializable {

    long getId();

    Long getReleaseId();

    int getRevisionNum();

    int getRevisionTrackingNum();

}
