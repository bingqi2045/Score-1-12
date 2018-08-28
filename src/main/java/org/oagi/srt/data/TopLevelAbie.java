package org.oagi.srt.data;

import lombok.Data;

@Data
public class TopLevelAbie {

    private long topLevelAbieId;
    private Long abieId;
    private long ownerUserId;
    private long releaseId;
    private int state;

}
