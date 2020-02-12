package org.oagi.srt.gateway.http.api.release_management.data;

import lombok.Data;
import org.oagi.srt.data.ReleaseState;

@Data
public class SimpleRelease {

    private long releaseId;
    private String releaseNum;
    private ReleaseState state;

}
