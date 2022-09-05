package org.oagi.score.repo.component.dt;

import java.math.BigInteger;

public class DeleteDtRepositoryResponse {

    private final String dtManifestId;

    public DeleteDtRepositoryResponse(String dtManifestId) {
        this.dtManifestId = dtManifestId;
    }

    public String getDtManifestId() {
        return dtManifestId;
    }
}
