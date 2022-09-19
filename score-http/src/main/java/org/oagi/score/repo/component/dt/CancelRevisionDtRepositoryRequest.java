package org.oagi.score.repo.component.dt;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

public class CancelRevisionDtRepositoryRequest extends RepositoryRequest {

    private final String dtManifestId;

    public CancelRevisionDtRepositoryRequest(AuthenticatedPrincipal user,
                                             String dtManifestId) {
        super(user);
        this.dtManifestId = dtManifestId;
    }

    public String getDtManifestId() {
        return dtManifestId;
    }
}
