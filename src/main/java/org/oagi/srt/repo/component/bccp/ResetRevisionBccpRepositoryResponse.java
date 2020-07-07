package org.oagi.srt.repo.component.bccp;

import java.math.BigInteger;

public class ResetRevisionBccpRepositoryResponse {

    private final BigInteger bccpManifestId;

    public ResetRevisionBccpRepositoryResponse(BigInteger bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }
}
