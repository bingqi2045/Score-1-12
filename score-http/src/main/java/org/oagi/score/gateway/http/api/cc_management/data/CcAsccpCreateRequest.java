package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

@Data
public class CcAsccpCreateRequest {

    private String releaseId;
    private String roleOfAccManifestId;
    private String asccpType;
    private String initialPropertyTerm;

}
