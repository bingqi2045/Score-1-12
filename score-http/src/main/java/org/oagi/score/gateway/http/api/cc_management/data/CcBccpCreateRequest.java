package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

@Data
public class CcBccpCreateRequest {

    private String releaseId;
    private String bdtManifestId;

}
