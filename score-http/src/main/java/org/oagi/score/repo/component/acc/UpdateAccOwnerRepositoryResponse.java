package org.oagi.score.repo.component.acc;

import java.math.BigInteger;

public class UpdateAccOwnerRepositoryResponse {

    private final String accManifestId;

    public UpdateAccOwnerRepositoryResponse(String accManifestId) {
        this.accManifestId = accManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
