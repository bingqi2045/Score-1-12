package org.oagi.srt.repo.component.acc;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;

public class UpdateAccOwnerRepositoryRequest extends RepositoryRequest {

    private final BigInteger accManifestId;
    private final BigInteger ownerId;

    public UpdateAccOwnerRepositoryRequest(AuthenticatedPrincipal user,
                                           BigInteger accManifestId,
                                           BigInteger ownerId) {
        super(user);
        this.accManifestId = accManifestId;
        this.ownerId = ownerId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }

    public BigInteger getOwnerId() {
        return ownerId;
    }
}
