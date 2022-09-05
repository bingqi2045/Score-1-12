package org.oagi.score.repo.component.bccp;

public class PurgeBccpRepositoryResponse {

    private final String bccpManifestId;

    public PurgeBccpRepositoryResponse(String bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }
}
