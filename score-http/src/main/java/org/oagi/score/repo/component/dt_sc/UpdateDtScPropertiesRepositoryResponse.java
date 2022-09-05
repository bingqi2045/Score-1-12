package org.oagi.score.repo.component.dt_sc;

public class UpdateDtScPropertiesRepositoryResponse {

    private final String dtScManifestId;

    public UpdateDtScPropertiesRepositoryResponse(String dtScManifestId) {
        this.dtScManifestId = dtScManifestId;
    }

    public String getDtScManifestId() {
        return dtScManifestId;
    }
}
