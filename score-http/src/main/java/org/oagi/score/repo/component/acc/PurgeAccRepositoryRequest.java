package org.oagi.score.repo.component.acc;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class PurgeAccRepositoryRequest extends RepositoryRequest {

    private final String accManifestId;

    public PurgeAccRepositoryRequest(AuthenticatedPrincipal user,
                                     String accManifestId) {
        super(user);
        this.accManifestId = accManifestId;
    }

    public PurgeAccRepositoryRequest(AuthenticatedPrincipal user,
                                     LocalDateTime localDateTime,
                                     String accManifestId) {
        super(user, localDateTime);
        this.accManifestId = accManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
