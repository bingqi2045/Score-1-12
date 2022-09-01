package org.oagi.score.repo.component.dt;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.util.List;

public class CreateCodeListRestrictionRepositoryRequest extends RepositoryRequest {

    private final BigInteger dtManifestId;
    private final String releaseId;

    private BigInteger codeListManifestId;



    public CreateCodeListRestrictionRepositoryRequest(AuthenticatedPrincipal user,
                                                      BigInteger dtManifestId, String releaseId) {
        super(user);
        this.dtManifestId = dtManifestId;
        this.releaseId = releaseId;
    }

    public BigInteger getDtManifestId() {
        return dtManifestId;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public BigInteger getCodeListManifestId() {
        return codeListManifestId;
    }

    public void setCodeListManifestId(BigInteger codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }
}
