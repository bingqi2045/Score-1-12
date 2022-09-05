package org.oagi.score.repo.component.ascc;

import java.math.BigInteger;

public class RefactorAsccRepositoryResponse {

    private final String asccManifestId;

    public RefactorAsccRepositoryResponse(String asccManifestId) {
        this.asccManifestId = asccManifestId;
    }

    public String getAsccManifestId() {
        return asccManifestId;
    }
}
