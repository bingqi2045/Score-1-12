package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

@Data
public class CcUpdateManifestRequest {
    private String accManifestId;
    private String bdtManifestId;
}
