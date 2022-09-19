package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

@Data
public class CreateBieFromExistingBieRequest {

    private String asbieHashPath;
    private String topLevelAsbiepId;
    private String asccpManifestId;
}