package org.oagi.score.repo.component.bcc;

public class RefactorBccRepositoryResponse {

    private final String bccManifestId;

    public RefactorBccRepositoryResponse(String bccManifestId) {
        this.bccManifestId = bccManifestId;
    }

    public String getBccManifestId() {
        return bccManifestId;
    }
}
