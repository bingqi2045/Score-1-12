package org.oagi.score.repo.component.bccp;

import java.math.BigInteger;

public class CancelRevisionBccpRepositoryResponse {

    private final String bccpManifestId;

    public CancelRevisionBccpRepositoryResponse(String bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }
}
