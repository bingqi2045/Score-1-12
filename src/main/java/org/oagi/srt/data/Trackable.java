package org.oagi.srt.data;

import java.io.Serializable;

public interface Trackable extends Serializable {

    public long getId();

    public Long getCurrentId();

    public Long getReleaseId();

    public int getRevisionNum();

    public int getRevisionTrackingNum();

}
