package org.oagi.score.repo.component.dt;

public class UpdateDtOwnerRepositoryResponse {

    private final String dtManifestId;

    public UpdateDtOwnerRepositoryResponse(String dtManifestId) {
        this.dtManifestId = dtManifestId;
    }

    public String getDtManifestId() {
        return dtManifestId;
    }
}
