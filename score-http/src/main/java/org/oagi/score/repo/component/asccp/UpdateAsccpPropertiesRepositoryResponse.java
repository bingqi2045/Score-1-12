package org.oagi.score.repo.component.asccp;

public class UpdateAsccpPropertiesRepositoryResponse {

    private final String asccpManifestId;

    public UpdateAsccpPropertiesRepositoryResponse(String asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }
}
