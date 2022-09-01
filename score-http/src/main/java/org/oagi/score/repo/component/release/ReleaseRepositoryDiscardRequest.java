package org.oagi.score.repo.component.release;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class ReleaseRepositoryDiscardRequest extends RepositoryRequest {

    private final String releaseId;

    public ReleaseRepositoryDiscardRequest(AuthenticatedPrincipal user, String releaseId) {
        super(user);
        this.releaseId = releaseId;
    }

    public ReleaseRepositoryDiscardRequest(AuthenticatedPrincipal user,
                                           LocalDateTime localDateTime,
                                           String releaseId) {
        super(user, localDateTime);
        this.releaseId = releaseId;
    }

    public String getReleaseId() {
        return releaseId;
    }
}
