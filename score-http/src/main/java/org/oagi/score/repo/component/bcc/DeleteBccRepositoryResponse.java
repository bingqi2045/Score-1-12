package org.oagi.score.repo.component.bcc;

import java.math.BigInteger;

public class DeleteBccRepositoryResponse {

    private final String bccManifestId;

    public DeleteBccRepositoryResponse(String bccManifestId) {
        this.bccManifestId = bccManifestId;
    }

    public String getBccManifestId() {
        return bccManifestId;
    }
}
