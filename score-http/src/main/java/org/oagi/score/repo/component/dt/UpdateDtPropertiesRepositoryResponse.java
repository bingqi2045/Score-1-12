package org.oagi.score.repo.component.dt;

public class UpdateDtPropertiesRepositoryResponse {

    private final String dtManifestId;

    public UpdateDtPropertiesRepositoryResponse(String dtManifestId) {
        this.dtManifestId = dtManifestId;
    }

    public String getDtManifestId() {
        return dtManifestId;
    }
}
