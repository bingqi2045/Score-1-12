package org.oagi.score.repo.component.acc;

public class DeleteAccRepositoryResponse {

    private final String accManifestId;

    public DeleteAccRepositoryResponse(String accManifestId) {
        this.accManifestId = accManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
