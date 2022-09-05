package org.oagi.score.repo.component.dt;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;

public class UpdateDtOwnerRepositoryRequest extends RepositoryRequest {

    private final String dtManifestId;
    private final String ownerId;

    public UpdateDtOwnerRepositoryRequest(AuthenticatedPrincipal user,
                                          String dtManifestId,
                                          String ownerId) {
        super(user);
        this.dtManifestId = dtManifestId;
        this.ownerId = ownerId;
    }

    public String getDtManifestId() {
        return dtManifestId;
    }

    public String getOwnerId() {
        return ownerId;
    }
}
