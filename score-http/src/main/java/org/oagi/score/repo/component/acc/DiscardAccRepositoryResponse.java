package org.oagi.score.repo.component.acc;

import java.math.BigInteger;

public class DiscardAccRepositoryResponse {

    private final BigInteger accManifestId;

    public DiscardAccRepositoryResponse(BigInteger accManifestId) {
        this.accManifestId = accManifestId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }
}
