package org.oagi.score.repo.component.bcc;

import java.math.BigInteger;

public class CreateBccRepositoryResponse {

    private final String bccManifestId;

    public CreateBccRepositoryResponse(String bccManifestId) {
        this.bccManifestId = bccManifestId;
    }

    public String getBccManifestId() {
        return bccManifestId;
    }
}
