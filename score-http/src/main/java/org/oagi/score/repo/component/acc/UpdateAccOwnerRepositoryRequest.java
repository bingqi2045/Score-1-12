package org.oagi.score.repo.component.acc;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

public class UpdateAccOwnerRepositoryRequest extends RepositoryRequest {

    private final String accManifestId;
    private final String ownerId;

    public UpdateAccOwnerRepositoryRequest(AuthenticatedPrincipal user,
                                           String accManifestId,
                                           String ownerId) {
        super(user);
        this.accManifestId = accManifestId;
        this.ownerId = ownerId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }

    public String getOwnerId() {
        return ownerId;
    }
}
