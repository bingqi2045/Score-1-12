package org.oagi.score.repo.component.dt;

public class ReviseDtRepositoryResponse {

    private final String dtManifestId;

    public ReviseDtRepositoryResponse(String dtManifestId) {
        this.dtManifestId = dtManifestId;
    }

    public String getDtManifestId() {
        return dtManifestId;
    }
}
