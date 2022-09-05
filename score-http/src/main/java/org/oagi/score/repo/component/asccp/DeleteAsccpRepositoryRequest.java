package org.oagi.score.repo.component.asccp;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class DeleteAsccpRepositoryRequest extends RepositoryRequest {

    private final String asccpManifestId;

    public DeleteAsccpRepositoryRequest(AuthenticatedPrincipal user,
                                        String asccpManifestId) {
        super(user);
        this.asccpManifestId = asccpManifestId;
    }

    public DeleteAsccpRepositoryRequest(AuthenticatedPrincipal user,
                                        LocalDateTime localDateTime,
                                        String asccpManifestId) {
        super(user, localDateTime);
        this.asccpManifestId = asccpManifestId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }
}
