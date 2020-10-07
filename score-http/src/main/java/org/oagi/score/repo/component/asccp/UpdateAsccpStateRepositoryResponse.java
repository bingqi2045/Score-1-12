package org.oagi.score.repo.component.asccp;

import org.oagi.score.data.RepositoryResponse;

import java.math.BigInteger;

public class UpdateAsccpStateRepositoryResponse extends RepositoryResponse {

    private final BigInteger asccpManifestId;

    public UpdateAsccpStateRepositoryResponse(BigInteger asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }
}
