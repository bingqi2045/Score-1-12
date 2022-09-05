package org.oagi.score.repo.component.dt;

public class UpdateDtStateRepositoryResponse {

    private final String dtManifestId;

    public UpdateDtStateRepositoryResponse(String dtManifestId) {
        this.dtManifestId = dtManifestId;
    }

    public String getDtManifestId() {
        return dtManifestId;
    }
}
