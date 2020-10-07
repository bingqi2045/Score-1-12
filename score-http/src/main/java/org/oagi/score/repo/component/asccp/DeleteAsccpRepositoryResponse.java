package org.oagi.score.repo.component.asccp;

import org.oagi.score.data.RepositoryResponse;

import java.math.BigInteger;

public class DeleteAsccpRepositoryResponse extends RepositoryResponse {

    private final BigInteger asccpManifestId;

    public DeleteAsccpRepositoryResponse(BigInteger asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }
}
