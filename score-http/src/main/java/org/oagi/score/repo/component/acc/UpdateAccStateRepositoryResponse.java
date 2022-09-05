package org.oagi.score.repo.component.acc;

public class UpdateAccStateRepositoryResponse {

    private final String accManifestId;

    public UpdateAccStateRepositoryResponse(String accManifestId) {
        this.accManifestId = accManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
