package org.oagi.score.repo.component.acc;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

public class CancelRevisionAccRepositoryRequest extends RepositoryRequest {

    private final String accManifestId;

    public CancelRevisionAccRepositoryRequest(AuthenticatedPrincipal user,
                                              String accManifestId) {
        super(user);
        this.accManifestId = accManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
