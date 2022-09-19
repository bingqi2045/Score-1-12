package org.oagi.score.repo.component.acc;

public class ReviseAccRepositoryResponse {

    private final String accManifestId;

    public ReviseAccRepositoryResponse(String accManifestId) {
        this.accManifestId = accManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
