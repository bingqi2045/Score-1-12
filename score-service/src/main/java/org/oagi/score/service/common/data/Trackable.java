package org.oagi.score.service.common.data;

import java.io.Serializable;
import java.math.BigInteger;

public interface Trackable extends Serializable {

    BigInteger getId();

    String getReleaseId();
    String getReleaseNum();

    String getLogId();
    int getRevisionNum();
    int getRevisionTrackingNum();


}
