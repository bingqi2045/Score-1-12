package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;

@Data
public class CcAsccpCreateRequest {

    private long releaseId;
    private long roleOfAccManifestId;

}
