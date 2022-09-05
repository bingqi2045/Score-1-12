package org.oagi.score.repo.component.dt;

import java.math.BigInteger;

public class CancelRevisionDtRepositoryResponse {

    private final String dtManifestId;

    public CancelRevisionDtRepositoryResponse(String dtManifestId) {
        this.dtManifestId = dtManifestId;
    }

    public String getDtManifestId() {
        return dtManifestId;
    }
}
