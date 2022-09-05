package org.oagi.score.repo.component.acc;

import org.oagi.score.data.RepositoryRequest;
import org.oagi.score.service.common.data.CcState;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class UpdateAccStateRepositoryRequest extends RepositoryRequest {

    private final String accManifestId;
    private final CcState fromState;
    private final CcState toState;

    public UpdateAccStateRepositoryRequest(AuthenticatedPrincipal user,
                                           String accManifestId,
                                           CcState fromState,
                                           CcState toState) {
        super(user);
        this.accManifestId = accManifestId;
        this.fromState = fromState;
        this.toState = toState;
    }

    public UpdateAccStateRepositoryRequest(AuthenticatedPrincipal user,
                                           LocalDateTime localDateTime,
                                           String accManifestId,
                                           CcState fromState,
                                           CcState toState) {
        super(user, localDateTime);
        this.accManifestId = accManifestId;
        this.fromState = fromState;
        this.toState = toState;
    }

    public String getAccManifestId() {
        return accManifestId;
    }

    public CcState getFromState() {
        return fromState;
    }

    public CcState getToState() {
        return toState;
    }
}
