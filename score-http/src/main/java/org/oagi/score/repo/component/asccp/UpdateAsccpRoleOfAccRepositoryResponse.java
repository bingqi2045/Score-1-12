package org.oagi.score.repo.component.asccp;

import java.math.BigInteger;

public class UpdateAsccpRoleOfAccRepositoryResponse {

    private final String asccpManifestId;
    private final String den;

    public UpdateAsccpRoleOfAccRepositoryResponse(String asccpManifestId, String den) {
        this.asccpManifestId = asccpManifestId;
        this.den = den;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }
    public String getDen() {
        return den;
    }
}
