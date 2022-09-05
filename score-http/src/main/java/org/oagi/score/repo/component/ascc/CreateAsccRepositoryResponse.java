package org.oagi.score.repo.component.ascc;

import java.math.BigInteger;

public class CreateAsccRepositoryResponse {

    private final String asccManifestId;

    public CreateAsccRepositoryResponse(String asccManifestId) {
        this.asccManifestId = asccManifestId;
    }

    public String getAsccManifestId() {
        return asccManifestId;
    }
}
