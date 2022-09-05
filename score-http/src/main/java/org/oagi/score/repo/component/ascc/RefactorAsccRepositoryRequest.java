package org.oagi.score.repo.component.ascc;

import org.oagi.score.data.RepositoryRequest;
import org.oagi.score.service.common.data.CcState;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class RefactorAsccRepositoryRequest extends RepositoryRequest {

    private final String asccManifestId;
    private final String accManifestId;

    public RefactorAsccRepositoryRequest(AuthenticatedPrincipal user, String asccManifestId, String accManifestId) {
        super(user);
        this.asccManifestId = asccManifestId;
        this.accManifestId = accManifestId;
    }

    public RefactorAsccRepositoryRequest(AuthenticatedPrincipal user, LocalDateTime localDateTime, String asccManifestId, String accManifestId) {
        super(user, localDateTime);
        this.asccManifestId = asccManifestId;
        this.accManifestId = accManifestId;
    }

    public String getAsccManifestId() {
        return asccManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
