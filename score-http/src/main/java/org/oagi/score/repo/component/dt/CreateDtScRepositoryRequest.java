package org.oagi.score.repo.component.dt;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class CreateDtScRepositoryRequest extends RepositoryRequest {

    private final String ownerDdtManifestId;


    public CreateDtScRepositoryRequest(AuthenticatedPrincipal user,
                                       String ownerDdtManifestId) {
        super(user);
        this.ownerDdtManifestId = ownerDdtManifestId;
    }

    public String getOwnerDdtManifestId() {
        return ownerDdtManifestId;
    }
}
