package org.oagi.score.repo.component.dt;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

public class CreateAgencyIdListRestrictionRepositoryRequest extends RepositoryRequest {

    private final String dtManifestId;
    private final String releaseId;
    
    private String agencyIdListManifestId;



    public CreateAgencyIdListRestrictionRepositoryRequest(AuthenticatedPrincipal user,
                                                          String dtManifestId, String releaseId) {
        super(user);
        this.dtManifestId = dtManifestId;
        this.releaseId = releaseId;
    }

    public String getDtManifestId() {
        return dtManifestId;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public String getAgencyIdListManifestId() {
        return agencyIdListManifestId;
    }

    public void setAgencyIdListManifestId(String agencyIdListManifestId) {
        this.agencyIdListManifestId = agencyIdListManifestId;
    }
}
