package org.oagi.score.repo.component.bccp;

import java.math.BigInteger;

public class DeleteBccpRepositoryResponse {

    private final String bccpManifestId;

    public DeleteBccpRepositoryResponse(String bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }
}
