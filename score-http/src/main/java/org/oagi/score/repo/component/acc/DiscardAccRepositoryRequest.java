package org.oagi.score.repo.component.acc;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class DiscardAccRepositoryRequest extends RepositoryRequest {

    private final BigInteger accManifestId;

    public DiscardAccRepositoryRequest(AuthenticatedPrincipal user,
                                       BigInteger accManifestId) {
        super(user);
        this.accManifestId = accManifestId;
    }

    public DiscardAccRepositoryRequest(AuthenticatedPrincipal user,
                                       LocalDateTime localDateTime,
                                       BigInteger accManifestId) {
        super(user, localDateTime);
        this.accManifestId = accManifestId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }
}
