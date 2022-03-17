package org.oagi.score.repo.component.bccp;

import java.math.BigInteger;

public class DiscardBccpRepositoryResponse {

    private final BigInteger bccpManifestId;

    public DiscardBccpRepositoryResponse(BigInteger bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }
}
