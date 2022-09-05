package org.oagi.score.repo.component.acc;

import java.math.BigInteger;

public class CancelRevisionAccRepositoryResponse {

    private final String accManifestId;

    public CancelRevisionAccRepositoryResponse(String accManifestId) {
        this.accManifestId = accManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
