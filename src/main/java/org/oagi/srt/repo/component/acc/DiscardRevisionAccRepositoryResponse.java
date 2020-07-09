package org.oagi.srt.repo.component.acc;

import java.math.BigInteger;

public class DiscardRevisionAccRepositoryResponse {

    private final BigInteger accManifestId;

    public DiscardRevisionAccRepositoryResponse(BigInteger accManifestId) {
        this.accManifestId = accManifestId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }
}
