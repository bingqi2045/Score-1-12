package org.oagi.score.repo.component.acc;

import org.oagi.score.data.RepositoryResponse;

import java.math.BigInteger;

public class UpdateAccOwnerRepositoryResponse extends RepositoryResponse {

    private final BigInteger accManifestId;

    public UpdateAccOwnerRepositoryResponse(BigInteger accManifestId) {
        this.accManifestId = accManifestId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }
}
