package org.oagi.srt.gateway.http.api.release_management.data;

import lombok.Data;

@Data
public class ReleaseDetail {
    private long releaseId;
    private String releaseNum;
    private String releaseNote;
    private long namespaceId;
}
