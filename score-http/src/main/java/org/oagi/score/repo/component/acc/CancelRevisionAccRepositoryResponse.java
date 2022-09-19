package org.oagi.score.repo.component.acc;

public class CancelRevisionAccRepositoryResponse {

    private final String accManifestId;

    public CancelRevisionAccRepositoryResponse(String accManifestId) {
        this.accManifestId = accManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
