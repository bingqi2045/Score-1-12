package org.oagi.score.repo.component.asccp;

import org.oagi.score.data.RepositoryResponse;

import java.math.BigInteger;

public class UpdateAsccpOwnerRepositoryResponse extends RepositoryResponse {

    private final BigInteger asccpManifestId;

    public UpdateAsccpOwnerRepositoryResponse(BigInteger asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }
}
