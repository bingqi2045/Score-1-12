package org.oagi.score.repo.component.bccp;

public class DeleteBccpRepositoryResponse {

    private final String bccpManifestId;

    public DeleteBccpRepositoryResponse(String bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }
}
