package org.oagi.srt.repo.component.bccp;

import java.math.BigInteger;

public class DiscardRevisionBccpRepositoryResponse {

    private final BigInteger bccpManifestId;

    public DiscardRevisionBccpRepositoryResponse(BigInteger bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }
}
