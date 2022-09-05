package org.oagi.score.repo.component.acc;

import java.math.BigInteger;

public class CreateAccRepositoryResponse {

    private final String accManifestId;

    public CreateAccRepositoryResponse(String accManifestId) {
        this.accManifestId = accManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
