package org.oagi.score.repo.component.dt_sc;

import java.math.BigInteger;

public class DeleteDtScRepositoryResponse {

    private final String dtScManifestId;

    public DeleteDtScRepositoryResponse(String dtScManifestId) {
        this.dtScManifestId = dtScManifestId;
    }

    public String getDtScManifestId() {
        return dtScManifestId;
    }
}
