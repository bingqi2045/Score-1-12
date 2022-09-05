package org.oagi.score.repo.component.asccp;

import java.math.BigInteger;

public class ReviseAsccpRepositoryResponse {

    private final String asccpManifestId;

    public ReviseAsccpRepositoryResponse(String asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }
}
