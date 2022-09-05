package org.oagi.score.repo.component.acc;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

public class ReviseAccRepositoryRequest extends RepositoryRequest {

    private final String accManifestId;

    public ReviseAccRepositoryRequest(AuthenticatedPrincipal user,
                                      String accManifestId) {
        super(user);
        this.accManifestId = accManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
