package org.oagi.score.repo.component.dt;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class CreateBdtRepositoryRequest extends RepositoryRequest {

    private final BigInteger basedDdtManifestId;
    private final String releaseId;
    private final String specId;

    private String initialPropertyTerm = "Property Term";

    public CreateBdtRepositoryRequest(AuthenticatedPrincipal user,
                                      BigInteger basedDdtManifestId, String releaseId, String specId) {
        super(user);
        this.basedDdtManifestId = basedDdtManifestId;
        this.releaseId = releaseId;
        this.specId = specId;
    }

    public BigInteger getBasedDdtManifestId() {
        return basedDdtManifestId;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public String getSpecId() {
        return specId;
    }

    public String getInitialPropertyTerm() {
        return initialPropertyTerm;
    }

    public void setInitialPropertyTerm(String initialPropertyTerm) {
        this.initialPropertyTerm = initialPropertyTerm;
    }
}
