package org.oagi.srt.repo.component.ascc;

import java.math.BigInteger;

public class UpdateAsccPropertiesRepositoryResponse {

    private final BigInteger asccManifestId;

    public UpdateAsccPropertiesRepositoryResponse(BigInteger asccManifestId) {
        this.asccManifestId = asccManifestId;
    }

    public BigInteger getAsccManifestId() {
        return asccManifestId;
    }
}
