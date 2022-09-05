package org.oagi.score.repo.component.bcc;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class RefactorBccRepositoryRequest extends RepositoryRequest {

    private final String bccManifestId;
    private final String accManifestId;

    public RefactorBccRepositoryRequest(AuthenticatedPrincipal user, String bccManifestId, String accManifestId) {
        super(user);
        this.bccManifestId = bccManifestId;
        this.accManifestId = accManifestId;
    }

    public RefactorBccRepositoryRequest(AuthenticatedPrincipal user, LocalDateTime localDateTime, String bccManifestId, String accManifestId) {
        super(user, localDateTime);
        this.bccManifestId = bccManifestId;
        this.accManifestId = accManifestId;
    }

    public String getBccManifestId() {
        return bccManifestId;
    }

    public String getAccManifestId() {
        return accManifestId;
    }
}
