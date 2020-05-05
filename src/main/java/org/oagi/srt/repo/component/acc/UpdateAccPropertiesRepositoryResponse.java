package org.oagi.srt.repo.component.acc;

import java.math.BigInteger;

public class UpdateAccPropertiesRepositoryResponse {

    private final BigInteger accManifestId;

    public UpdateAccPropertiesRepositoryResponse(BigInteger accManifestId) {
        this.accManifestId = accManifestId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }
}
