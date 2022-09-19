package org.oagi.score.repo.component.bccp;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class DeleteBccpRepositoryRequest extends RepositoryRequest {

    private final String bccpManifestId;

    public DeleteBccpRepositoryRequest(AuthenticatedPrincipal user,
                                       String bccpManifestId) {
        super(user);
        this.bccpManifestId = bccpManifestId;
    }

    public DeleteBccpRepositoryRequest(AuthenticatedPrincipal user,
                                       LocalDateTime localDateTime,
                                       String bccpManifestId) {
        super(user, localDateTime);
        this.bccpManifestId = bccpManifestId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }
}
