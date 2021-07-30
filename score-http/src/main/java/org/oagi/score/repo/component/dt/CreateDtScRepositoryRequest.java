package org.oagi.score.repo.component.dt;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class CreateDtScRepositoryRequest extends RepositoryRequest {

    private final BigInteger ownerDdtManifestId;
    private final BigInteger targetDdtManifestId;


    public CreateDtScRepositoryRequest(AuthenticatedPrincipal user,
                                       BigInteger ownerDdtManifestId, BigInteger targetDdtManifestId) {
        super(user);
        this.ownerDdtManifestId = ownerDdtManifestId;
        this.targetDdtManifestId = targetDdtManifestId;
    }

    public BigInteger getOwnerDdtManifestId() {
        return ownerDdtManifestId;
    }

    public BigInteger getTargetDdtManifestId() {
        return targetDdtManifestId;
    }
}
