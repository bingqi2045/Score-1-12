package org.oagi.score.repo.component.bccp;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

public class ReviseBccpRepositoryRequest extends RepositoryRequest {

    private final String bccpManifestId;

    public ReviseBccpRepositoryRequest(AuthenticatedPrincipal user,
                                       String bccpManifestId) {
        super(user);
        this.bccpManifestId = bccpManifestId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }
}
