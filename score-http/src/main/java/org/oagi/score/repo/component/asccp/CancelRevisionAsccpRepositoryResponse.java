package org.oagi.score.repo.component.asccp;

public class CancelRevisionAsccpRepositoryResponse {

    private final String asccpManifestId;

    public CancelRevisionAsccpRepositoryResponse(String asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }
}
