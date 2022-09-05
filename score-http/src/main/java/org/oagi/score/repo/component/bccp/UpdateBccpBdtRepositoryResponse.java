package org.oagi.score.repo.component.bccp;

import java.math.BigInteger;

public class UpdateBccpBdtRepositoryResponse {

    private final String bccpManifestId;

    private final String den;

    public UpdateBccpBdtRepositoryResponse(String bccpManifestId, String den) {
        this.bccpManifestId = bccpManifestId;
        this.den = den;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }

    public String getDen() {
        return den;
    }
}
