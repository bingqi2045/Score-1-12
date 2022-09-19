package org.oagi.score.service.common.data;

import java.io.Serializable;

public interface Trackable<T> extends Serializable {

    T getId();

    String getReleaseId();
    String getReleaseNum();

    String getLogId();
    int getRevisionNum();
    int getRevisionTrackingNum();


}
