package org.oagi.score.repo.component.asccp;

import java.math.BigInteger;

public class UpdateAsccpOwnerRepositoryResponse {

    private final String asccpManifestId;

    public UpdateAsccpOwnerRepositoryResponse(String asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }
}
