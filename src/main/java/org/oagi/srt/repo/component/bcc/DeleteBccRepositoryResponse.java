package org.oagi.srt.repo.component.bcc;

import java.math.BigInteger;

public class DeleteBccRepositoryResponse {

    private final BigInteger bccManifestId;

    public DeleteBccRepositoryResponse(BigInteger bccManifestId) {
        this.bccManifestId = bccManifestId;
    }

    public BigInteger getBccManifestId() {
        return bccManifestId;
    }
}
