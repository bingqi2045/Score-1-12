package org.oagi.score.repo.component.bccp;

import java.math.BigInteger;

public class ReviseBccpRepositoryResponse {

    private final String bccpManifestId;

    public ReviseBccpRepositoryResponse(String bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }
}
