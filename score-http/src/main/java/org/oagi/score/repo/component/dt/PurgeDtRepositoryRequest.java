package org.oagi.score.repo.component.dt;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class PurgeDtRepositoryRequest extends RepositoryRequest {

    private final String dtManifestId;

    public PurgeDtRepositoryRequest(AuthenticatedPrincipal user,
                                    String dtManifestId) {
        super(user);
        this.dtManifestId = dtManifestId;
    }

    public PurgeDtRepositoryRequest(AuthenticatedPrincipal user,
                                    LocalDateTime localDateTime,
                                    String dtManifestId) {
        super(user, localDateTime);
        this.dtManifestId = dtManifestId;
    }

    public String getDtManifestId() {
        return dtManifestId;
    }
}
