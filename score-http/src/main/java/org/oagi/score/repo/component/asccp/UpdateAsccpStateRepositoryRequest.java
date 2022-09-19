package org.oagi.score.repo.component.asccp;

import org.oagi.score.data.RepositoryRequest;
import org.oagi.score.service.common.data.CcState;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class UpdateAsccpStateRepositoryRequest extends RepositoryRequest {

    private final String asccpManifestId;
    private final CcState fromState;
    private final CcState toState;

    public UpdateAsccpStateRepositoryRequest(AuthenticatedPrincipal user,
                                             String asccpManifestId,
                                             CcState fromState,
                                             CcState toState) {
        super(user);
        this.asccpManifestId = asccpManifestId;
        this.fromState = fromState;
        this.toState = toState;
    }

    public UpdateAsccpStateRepositoryRequest(AuthenticatedPrincipal user,
                                             LocalDateTime localDateTime,
                                             String asccpManifestId,
                                             CcState fromState,
                                             CcState toState) {
        super(user, localDateTime);
        this.asccpManifestId = asccpManifestId;
        this.fromState = fromState;
        this.toState = toState;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }

    public CcState getFromState() {
        return fromState;
    }

    public CcState getToState() {
        return toState;
    }
}
