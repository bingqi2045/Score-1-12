package org.oagi.score.repo.component.bcc;

import java.math.BigInteger;

public class UpdateBccPropertiesRepositoryResponse {

    private final String bccManifestId;

    public UpdateBccPropertiesRepositoryResponse(String bccManifestId) {
        this.bccManifestId = bccManifestId;
    }

    public String getBccManifestId() {
        return bccManifestId;
    }
}
