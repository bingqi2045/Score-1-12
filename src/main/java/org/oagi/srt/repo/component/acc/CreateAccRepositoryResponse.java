package org.oagi.srt.repo.component.acc;

import java.math.BigInteger;

public class CreateAccRepositoryResponse {

    private final BigInteger accManifestId;

    public CreateAccRepositoryResponse(BigInteger accManifestId) {
        this.accManifestId = accManifestId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }
}
