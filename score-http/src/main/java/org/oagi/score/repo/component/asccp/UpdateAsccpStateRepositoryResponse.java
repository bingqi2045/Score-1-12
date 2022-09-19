package org.oagi.score.repo.component.asccp;

public class UpdateAsccpStateRepositoryResponse {

    private final String asccpManifestId;

    public UpdateAsccpStateRepositoryResponse(String asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }
}
