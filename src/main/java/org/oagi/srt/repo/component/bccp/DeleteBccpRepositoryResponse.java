package org.oagi.srt.repo.component.bccp;

import java.math.BigInteger;

public class DeleteBccpRepositoryResponse {

    private final BigInteger bccpManifestId;

    public DeleteBccpRepositoryResponse(BigInteger bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }
}
