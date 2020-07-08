package org.oagi.srt.repo.component.acc;

import java.math.BigInteger;

public class ResetRevisionAccRepositoryResponse {

    private final BigInteger accManifestId;

    public ResetRevisionAccRepositoryResponse(BigInteger accManifestId) {
        this.accManifestId = accManifestId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }
}
