package org.oagi.score.repo.component.bcc;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class DeleteBccRepositoryRequest extends RepositoryRequest {

    private final String bccManifestId;

    public DeleteBccRepositoryRequest(AuthenticatedPrincipal user,
                                      String bccManifestId) {
        super(user);
        this.bccManifestId = bccManifestId;
    }

    public DeleteBccRepositoryRequest(AuthenticatedPrincipal user,
                                      LocalDateTime localDateTime,
                                      String bccManifestId) {
        super(user, localDateTime);
        this.bccManifestId = bccManifestId;
    }

    public String getBccManifestId() {
        return bccManifestId;
    }
}
