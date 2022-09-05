package org.oagi.score.repo.component.asccp;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;

public class UpdateAsccpOwnerRepositoryRequest extends RepositoryRequest {

    private final String asccpManifestId;
    private final String ownerId;

    public UpdateAsccpOwnerRepositoryRequest(AuthenticatedPrincipal user,
                                             String asccpManifestId,
                                             String ownerId) {
        super(user);
        this.asccpManifestId = asccpManifestId;
        this.ownerId = ownerId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }

    public String getOwnerId() {
        return ownerId;
    }
}
