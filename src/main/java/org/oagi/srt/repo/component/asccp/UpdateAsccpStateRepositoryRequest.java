package org.oagi.srt.repo.component.asccp;

import org.oagi.srt.data.RepositoryRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateAsccpStateRepositoryRequest extends RepositoryRequest {

    private final BigInteger asccpManifestId;
    private final CcState fromState;
    private final CcState toState;

    public UpdateAsccpStateRepositoryRequest(AuthenticatedPrincipal user,
                                             BigInteger asccpManifestId,
                                             CcState fromState,
                                             CcState toState) {
        super(user);
        this.asccpManifestId = asccpManifestId;
        this.fromState = fromState;
        this.toState = toState;
    }

    public UpdateAsccpStateRepositoryRequest(AuthenticatedPrincipal user,
                                             LocalDateTime localDateTime,
                                             BigInteger asccpManifestId,
                                             CcState fromState,
                                             CcState toState) {
        super(user, localDateTime);
        this.asccpManifestId = asccpManifestId;
        this.fromState = fromState;
        this.toState = toState;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }

    public CcState getFromState() {
        return fromState;
    }

    public CcState getToState() {
        return toState;
    }
}
