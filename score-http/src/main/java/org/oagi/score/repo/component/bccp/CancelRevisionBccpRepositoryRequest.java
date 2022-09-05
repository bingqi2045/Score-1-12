package org.oagi.score.repo.component.bccp;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;

public class CancelRevisionBccpRepositoryRequest extends RepositoryRequest {

    private final String bccpManifestId;

    public CancelRevisionBccpRepositoryRequest(AuthenticatedPrincipal user,
                                               String bccpManifestId) {
        super(user);
        this.bccpManifestId = bccpManifestId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }
}
