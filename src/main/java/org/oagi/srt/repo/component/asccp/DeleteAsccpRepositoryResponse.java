package org.oagi.srt.repo.component.asccp;

import java.math.BigInteger;

public class DeleteAsccpRepositoryResponse {

    private final BigInteger bccpManifestId;

    public DeleteAsccpRepositoryResponse(BigInteger bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }
}
