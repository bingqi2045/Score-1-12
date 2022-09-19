package org.oagi.score.repo.component.dt;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class DeleteDtRepositoryRequest extends RepositoryRequest {

    private final String dtManifestId;

    public DeleteDtRepositoryRequest(AuthenticatedPrincipal user,
                                     String dtManifestId) {
        super(user);
        this.dtManifestId = dtManifestId;
    }

    public DeleteDtRepositoryRequest(AuthenticatedPrincipal user,
                                     LocalDateTime localDateTime,
                                     String dtManifestId) {
        super(user, localDateTime);
        this.dtManifestId = dtManifestId;
    }

    public String getDtManifestId() {
        return dtManifestId;
    }
}
