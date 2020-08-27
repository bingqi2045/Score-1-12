package org.oagi.score.repo.component.asccp;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;

public class DiscardRevisionAsccpRepositoryRequest extends RepositoryRequest {

    private final BigInteger asccpManifestId;

    public DiscardRevisionAsccpRepositoryRequest(AuthenticatedPrincipal user,
                                                 BigInteger asccpManifestId) {
        super(user);
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }
}