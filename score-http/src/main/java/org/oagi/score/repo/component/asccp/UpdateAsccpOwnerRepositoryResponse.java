package org.oagi.score.repo.component.asccp;

public class UpdateAsccpOwnerRepositoryResponse {

    private final String asccpManifestId;

    public UpdateAsccpOwnerRepositoryResponse(String asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }
}
