package org.oagi.score.repo.component.asccp;

import java.math.BigInteger;

public class DeleteAsccpRepositoryResponse {

    private final String asccpManifestId;

    public DeleteAsccpRepositoryResponse(String asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }
}
