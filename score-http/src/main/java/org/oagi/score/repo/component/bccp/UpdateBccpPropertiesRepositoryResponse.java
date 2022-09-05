package org.oagi.score.repo.component.bccp;

public class UpdateBccpPropertiesRepositoryResponse {

    private final String bccpManifestId;

    public UpdateBccpPropertiesRepositoryResponse(String bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }
}
