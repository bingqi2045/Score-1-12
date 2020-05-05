package org.oagi.srt.repo.component.asccp;

import java.math.BigInteger;

public class DeleteAsccpRepositoryResponse {

    private final BigInteger asccpManifestId;

    public DeleteAsccpRepositoryResponse(BigInteger asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }
}
