package org.oagi.srt.repo.component.asccp;

import java.math.BigInteger;

public class UpdateAsccpRoleOfAccRepositoryResponse {

    private final BigInteger asccpManifestId;

    public UpdateAsccpRoleOfAccRepositoryResponse(BigInteger asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }
}
