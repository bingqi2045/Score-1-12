package org.oagi.srt.repo.component.asccp;

import java.math.BigInteger;

public class UpdateAsccpOwnerRepositoryResponse {

    private final BigInteger asccpManifestId;

    public UpdateAsccpOwnerRepositoryResponse(BigInteger asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }
}
