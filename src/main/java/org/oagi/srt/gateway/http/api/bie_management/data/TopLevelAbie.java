package org.oagi.srt.gateway.http.api.bie_management.data;

import lombok.Data;

@Data
public class TopLevelAbie {

    private long topLevelAbieId;
    private Long abieId;
    private long ownerUserId;
    private long releaseId;
    private int state;

}
