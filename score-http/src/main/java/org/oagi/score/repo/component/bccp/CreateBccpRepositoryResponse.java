package org.oagi.score.repo.component.bccp;

import java.math.BigInteger;

public class CreateBccpRepositoryResponse {

    private final String bccpManifestId;

    public CreateBccpRepositoryResponse(String bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }
}
