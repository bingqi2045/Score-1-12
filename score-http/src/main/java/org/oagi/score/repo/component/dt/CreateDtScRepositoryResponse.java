package org.oagi.score.repo.component.dt;

public class CreateDtScRepositoryResponse {

    private final String dtScManifestId;

    public CreateDtScRepositoryResponse(String dtScManifestId) {
        this.dtScManifestId = dtScManifestId;
    }

    public String getDtScManifestId() {
        return dtScManifestId;
    }
}
