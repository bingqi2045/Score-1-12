package org.oagi.score.repo.component.asccp;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class PurgeAsccpRepositoryRequest extends RepositoryRequest {

    private final String asccpManifestId;
    private boolean ignoreState;

    public PurgeAsccpRepositoryRequest(AuthenticatedPrincipal user,
                                       String asccpManifestId) {
        super(user);
        this.asccpManifestId = asccpManifestId;
    }

    public PurgeAsccpRepositoryRequest(AuthenticatedPrincipal user,
                                       LocalDateTime localDateTime,
                                       String asccpManifestId) {
        super(user, localDateTime);
        this.asccpManifestId = asccpManifestId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }

    public boolean isIgnoreState() {
        return ignoreState;
    }

    public void setIgnoreState(boolean ignoreState) {
        this.ignoreState = ignoreState;
    }
}
