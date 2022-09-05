package org.oagi.score.repo.component.acc;

public class UpdateAccBasedAccRepositoryResponse {

    private final String accManifestId;

    public UpdateAccBasedAccRepositoryResponse(String accManifestId) {
        this.accManifestId = accManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
