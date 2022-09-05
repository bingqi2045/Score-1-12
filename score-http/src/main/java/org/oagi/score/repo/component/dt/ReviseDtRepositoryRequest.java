package org.oagi.score.repo.component.dt;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;

public class ReviseDtRepositoryRequest extends RepositoryRequest {

    private final String dtManifestId;

    public ReviseDtRepositoryRequest(AuthenticatedPrincipal user,
                                     String dtManifestId) {
        super(user);
        this.dtManifestId = dtManifestId;
    }

    public String getDtManifestId() {
        return dtManifestId;
    }
}
