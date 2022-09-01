package org.oagi.score.repo.component.acc;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;

public class UpdateAccOwnerRepositoryRequest extends RepositoryRequest {

    private final BigInteger accManifestId;
    private final String ownerId;

    public UpdateAccOwnerRepositoryRequest(AuthenticatedPrincipal user,
                                           BigInteger accManifestId,
                                           String ownerId) {
        super(user);
        this.accManifestId = accManifestId;
        this.ownerId = ownerId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }

    public String getOwnerId() {
        return ownerId;
    }
}
