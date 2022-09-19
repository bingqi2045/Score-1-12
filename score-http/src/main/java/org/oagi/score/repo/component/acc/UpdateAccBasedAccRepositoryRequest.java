package org.oagi.score.repo.component.acc;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class UpdateAccBasedAccRepositoryRequest extends RepositoryRequest {

    private final String accManifestId;
    private final String basedAccManifestId;

    public UpdateAccBasedAccRepositoryRequest(AuthenticatedPrincipal user,
                                              String accManifestId,
                                              String basedAccManifestId) {
        super(user);
        this.accManifestId = accManifestId;
        this.basedAccManifestId = basedAccManifestId;
    }

    public UpdateAccBasedAccRepositoryRequest(AuthenticatedPrincipal user,
                                              LocalDateTime localDateTime,
                                              String accManifestId,
                                              String basedAccManifestId) {
        super(user, localDateTime);
        this.accManifestId = accManifestId;
        this.basedAccManifestId = basedAccManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }

    public String getBasedAccManifestId() {
        return basedAccManifestId;
    }
}
