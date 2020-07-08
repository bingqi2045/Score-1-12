package org.oagi.srt.repo.component.asccp;

import java.math.BigInteger;

public class ResetRevisionAsccpRepositoryResponse {

    private final BigInteger asccpManifestId;

    public ResetRevisionAsccpRepositoryResponse(BigInteger asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }
}
