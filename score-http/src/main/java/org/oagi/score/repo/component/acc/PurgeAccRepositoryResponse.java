package org.oagi.score.repo.component.acc;

public class PurgeAccRepositoryResponse {

    private final String accManifestId;

    public PurgeAccRepositoryResponse(String accManifestId) {
        this.accManifestId = accManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
