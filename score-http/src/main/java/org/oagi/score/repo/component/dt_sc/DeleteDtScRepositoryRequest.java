package org.oagi.score.repo.component.dt_sc;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class DeleteDtScRepositoryRequest extends RepositoryRequest {

    private final String dtScManifestId;

    public DeleteDtScRepositoryRequest(AuthenticatedPrincipal user,
                                       String dtScManifestId) {
        super(user);
        this.dtScManifestId = dtScManifestId;
    }

    public DeleteDtScRepositoryRequest(AuthenticatedPrincipal user,
                                       LocalDateTime localDateTime,
                                       String dtScManifestId) {
        super(user, localDateTime);
        this.dtScManifestId = dtScManifestId;
    }

    public String getDtScManifestId() {
        return dtScManifestId;
    }
}
