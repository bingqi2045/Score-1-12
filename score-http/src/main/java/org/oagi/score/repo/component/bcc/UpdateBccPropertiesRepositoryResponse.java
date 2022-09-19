package org.oagi.score.repo.component.bcc;

public class UpdateBccPropertiesRepositoryResponse {

    private final String bccManifestId;

    public UpdateBccPropertiesRepositoryResponse(String bccManifestId) {
        this.bccManifestId = bccManifestId;
    }

    public String getBccManifestId() {
        return bccManifestId;
    }
}
