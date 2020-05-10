package org.oagi.srt.data;

import java.io.Serializable;
import java.math.BigInteger;

public interface Trackable extends Serializable {

    BigInteger getId();

    BigInteger getReleaseId();
    String getReleaseNum();

    BigInteger getRevisionId();
    int getRevisionNum();
    int getRevisionTrackingNum();


}
