package org.oagi.score.repo.component.bccp;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;

public class UpdateBccpOwnerRepositoryRequest extends RepositoryRequest {

    private final String bccpManifestId;
    private final String ownerId;

    public UpdateBccpOwnerRepositoryRequest(AuthenticatedPrincipal user,
                                            String bccpManifestId,
                                            String ownerId) {
        super(user);
        this.bccpManifestId = bccpManifestId;
        this.ownerId = ownerId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }

    public String getOwnerId() {
        return ownerId;
    }
}
