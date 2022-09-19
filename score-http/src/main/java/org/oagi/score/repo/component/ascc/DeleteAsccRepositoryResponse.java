package org.oagi.score.repo.component.ascc;

public class DeleteAsccRepositoryResponse {

    private final String asccManifestId;

    public DeleteAsccRepositoryResponse(String asccManifestId) {
        this.asccManifestId = asccManifestId;
    }

    public String getAsccManifestId() {
        return asccManifestId;
    }
}
