package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

@Data
public class CcAppendRequest {
    private String releaseId;
    private String accManifestId;
    private String asccpManifestId;
    private String bccpManifestId;
    private boolean attribute;
    private int pos;
}
