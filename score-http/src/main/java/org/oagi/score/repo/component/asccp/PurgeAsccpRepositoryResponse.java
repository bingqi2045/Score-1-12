package org.oagi.score.repo.component.asccp;

public class PurgeAsccpRepositoryResponse {

    private final String asccpManifestId;

    public PurgeAsccpRepositoryResponse(String asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }
}
