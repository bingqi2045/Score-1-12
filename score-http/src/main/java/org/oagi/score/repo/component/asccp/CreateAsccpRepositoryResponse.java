package org.oagi.score.repo.component.asccp;

public class CreateAsccpRepositoryResponse {

    private final String asccpManifestId;

    public CreateAsccpRepositoryResponse(String asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }
}
