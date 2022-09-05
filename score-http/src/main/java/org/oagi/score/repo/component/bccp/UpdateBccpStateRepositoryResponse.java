package org.oagi.score.repo.component.bccp;

import java.math.BigInteger;

public class UpdateBccpStateRepositoryResponse {

    private final String bccpManifestId;

    public UpdateBccpStateRepositoryResponse(String bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }
}
