package org.oagi.srt.repo.component.asccp;

import java.math.BigInteger;

public class DiscardRevisionAsccpRepositoryResponse {

    private final BigInteger asccpManifestId;

    public DiscardRevisionAsccpRepositoryResponse(BigInteger asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }
}
