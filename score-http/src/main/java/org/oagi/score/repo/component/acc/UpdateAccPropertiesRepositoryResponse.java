package org.oagi.score.repo.component.acc;

public class UpdateAccPropertiesRepositoryResponse {

    private final String accManifestId;

    public UpdateAccPropertiesRepositoryResponse(String accManifestId) {
        this.accManifestId = accManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
