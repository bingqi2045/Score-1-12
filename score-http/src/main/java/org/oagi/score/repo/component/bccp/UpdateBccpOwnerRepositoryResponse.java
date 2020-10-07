package org.oagi.score.repo.component.bccp;

import org.oagi.score.data.RepositoryResponse;

import java.math.BigInteger;

public class UpdateBccpOwnerRepositoryResponse extends RepositoryResponse {

    private final BigInteger bccpManifestId;

    public UpdateBccpOwnerRepositoryResponse(BigInteger bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }
}
