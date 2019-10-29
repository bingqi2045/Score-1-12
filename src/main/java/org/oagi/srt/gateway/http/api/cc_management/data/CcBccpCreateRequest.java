package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;

@Data
public class CcBccpCreateRequest {

    private long releaseId;
    private long bdtManifestId;

}
