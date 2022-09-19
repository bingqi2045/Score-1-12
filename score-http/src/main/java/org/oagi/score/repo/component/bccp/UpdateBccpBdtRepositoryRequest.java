package org.oagi.score.repo.component.bccp;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class UpdateBccpBdtRepositoryRequest extends RepositoryRequest {

    private final String bccpManifestId;
    private final String bdtManifestId;

    public UpdateBccpBdtRepositoryRequest(AuthenticatedPrincipal user,
                                          String bccpManifestId,
                                          String bdtManifestId) {
        super(user);
        this.bccpManifestId = bccpManifestId;
        this.bdtManifestId = bdtManifestId;
    }

    public UpdateBccpBdtRepositoryRequest(AuthenticatedPrincipal user,
                                          LocalDateTime localDateTime,
                                          String bccpManifestId,
                                          String bdtManifestId) {
        super(user, localDateTime);
        this.bccpManifestId = bccpManifestId;
        this.bdtManifestId = bdtManifestId;
    }

    public String getBccpManifestId() {
        return bccpManifestId;
    }

    public String getBdtManifestId() {
        return bdtManifestId;
    }
}
