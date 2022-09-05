package org.oagi.score.repo.component.dt;

import java.math.BigInteger;

public class CreateBdtRepositoryResponse {

    private final String bdtManifestId;

    public CreateBdtRepositoryResponse(String bdtManifestId) {
        this.bdtManifestId = bdtManifestId;
    }

    public String getBdtManifestId() {
        return bdtManifestId;
    }
}
