package org.oagi.srt.repo.component.bcc;

import java.math.BigInteger;

public class CreateBccRepositoryResponse {

    private final BigInteger bccManifestId;

    public CreateBccRepositoryResponse(BigInteger bccManifestId) {
        this.bccManifestId = bccManifestId;
    }

    public BigInteger getBccManifestId() {
        return bccManifestId;
    }
}
