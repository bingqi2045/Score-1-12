package org.oagi.score.repo.component.asccp;

import java.math.BigInteger;

public class DiscardAsccpRepositoryResponse {

    private final BigInteger asccpManifestId;

    public DiscardAsccpRepositoryResponse(BigInteger asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }
}
