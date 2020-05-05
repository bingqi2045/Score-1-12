package org.oagi.srt.repo.component.bccp;

import java.math.BigInteger;

public class UpdateBccpBdtRepositoryResponse {

    private final BigInteger bccpManifestId;

    public UpdateBccpBdtRepositoryResponse(BigInteger bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }
}
