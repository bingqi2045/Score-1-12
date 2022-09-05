package org.oagi.score.repo.component.bccp;

public class ReviseBccpRepositoryResponse {

    private final String bccpManifestId;

    public ReviseBccpRepositoryResponse(String bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }
}
