package org.oagi.score.repo.component.asccp;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

public class ReviseAsccpRepositoryRequest extends RepositoryRequest {

    private final String asccpManifestId;

    public ReviseAsccpRepositoryRequest(AuthenticatedPrincipal user,
                                        String asccpManifestId) {
        super(user);
        this.asccpManifestId = asccpManifestId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }
}
