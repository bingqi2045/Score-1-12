package org.oagi.srt.repo.component.bccp;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;

public class DiscardRevisionBccpRepositoryRequest extends RepositoryRequest {

    private final BigInteger bccpManifestId;

    public DiscardRevisionBccpRepositoryRequest(AuthenticatedPrincipal user,
                                                BigInteger bccpManifestId) {
        super(user);
        this.bccpManifestId = bccpManifestId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }
}
