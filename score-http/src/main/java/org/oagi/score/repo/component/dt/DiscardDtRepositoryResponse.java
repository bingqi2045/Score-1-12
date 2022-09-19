package org.oagi.score.repo.component.dt;

public class DiscardDtRepositoryResponse {

    private final String dtManifestId;

    public DiscardDtRepositoryResponse(String dtManifestId) {
        this.dtManifestId = dtManifestId;
    }

    public String getDtManifestId() {
        return dtManifestId;
    }
}
