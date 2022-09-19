package org.oagi.score.repo.component.ascc;

public class RefactorAsccRepositoryResponse {

    private final String asccManifestId;

    public RefactorAsccRepositoryResponse(String asccManifestId) {
        this.asccManifestId = asccManifestId;
    }

    public String getAsccManifestId() {
        return asccManifestId;
    }
}
