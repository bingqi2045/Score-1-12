package org.oagi.score.repo.component.bccp;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class CreateBccpRepositoryRequest extends RepositoryRequest {

    private final String bdtManifestId;
    private final String releaseId;

    private String initialPropertyTerm = "Property Term";

    public CreateBccpRepositoryRequest(AuthenticatedPrincipal user,
                                       String bdtManifestId, String releaseId) {
        super(user);
        this.bdtManifestId = bdtManifestId;
        this.releaseId = releaseId;
    }

    public CreateBccpRepositoryRequest(AuthenticatedPrincipal user,
                                       LocalDateTime localDateTime,
                                       String bdtManifestId, String releaseId) {
        super(user, localDateTime);
        this.bdtManifestId = bdtManifestId;
        this.releaseId = releaseId;
    }

    public String getBdtManifestId() {
        return bdtManifestId;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public String getInitialPropertyTerm() {
        return initialPropertyTerm;
    }

    public void setInitialPropertyTerm(String initialPropertyTerm) {
        this.initialPropertyTerm = initialPropertyTerm;
    }
}
