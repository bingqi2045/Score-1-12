package org.oagi.score.repo.component.ascc;

public class UpdateAsccPropertiesRepositoryResponse {

    private final String asccManifestId;

    public UpdateAsccPropertiesRepositoryResponse(String asccManifestId) {
        this.asccManifestId = asccManifestId;
    }

    public String getAsccManifestId() {
        return asccManifestId;
    }
}
