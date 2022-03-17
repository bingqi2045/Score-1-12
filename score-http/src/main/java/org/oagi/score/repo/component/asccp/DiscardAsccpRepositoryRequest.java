package org.oagi.score.repo.component.asccp;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class DiscardAsccpRepositoryRequest extends RepositoryRequest {

    private final BigInteger asccpManifestId;

    public DiscardAsccpRepositoryRequest(AuthenticatedPrincipal user,
                                         BigInteger asccpManifestId) {
        super(user);
        this.asccpManifestId = asccpManifestId;
    }

    public DiscardAsccpRepositoryRequest(AuthenticatedPrincipal user,
                                         LocalDateTime localDateTime,
                                         BigInteger asccpManifestId) {
        super(user, localDateTime);
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }
}
