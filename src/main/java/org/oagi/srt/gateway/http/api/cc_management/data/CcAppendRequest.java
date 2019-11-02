package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;

@Data
public class CcAppendRequest {
    private Long accManifestId;
    private Long asccManifestId;
    private Long bccManifestId;
}
