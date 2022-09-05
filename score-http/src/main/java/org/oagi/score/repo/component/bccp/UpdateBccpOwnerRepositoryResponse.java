package org.oagi.score.repo.component.bccp;

import java.math.BigInteger;

public class UpdateBccpOwnerRepositoryResponse {

    private final String bccpManifestId;

    public UpdateBccpOwnerRepositoryResponse(String bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }
}
