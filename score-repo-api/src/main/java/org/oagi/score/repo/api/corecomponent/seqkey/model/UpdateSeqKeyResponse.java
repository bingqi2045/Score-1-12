package org.oagi.score.repo.api.corecomponent.seqkey.model;

import org.oagi.score.repo.api.base.Response;

import java.math.BigInteger;

public class UpdateSeqKeyResponse extends Response {

    private final BigInteger accManifestId;

    public UpdateSeqKeyResponse(BigInteger accManifestId) {
        this.accManifestId = accManifestId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }

}
