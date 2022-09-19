package org.oagi.score.repo.component.acc;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class DeleteAccRepositoryRequest extends RepositoryRequest {

    private final String accManifestId;

    public DeleteAccRepositoryRequest(AuthenticatedPrincipal user,
                                      String accManifestId) {
        super(user);
        this.accManifestId = accManifestId;
    }

    public DeleteAccRepositoryRequest(AuthenticatedPrincipal user,
                                      LocalDateTime localDateTime,
                                      String accManifestId) {
        super(user, localDateTime);
        this.accManifestId = accManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
