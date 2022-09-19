package org.oagi.score.repo.component.dt;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.util.List;

public class CreatePrimitiveRestrictionRepositoryRequest extends RepositoryRequest {

    private final String dtManifestId;
    private final String releaseId;

    private String primitive;
    private List<String> xbtManifestIdList;



    public CreatePrimitiveRestrictionRepositoryRequest(AuthenticatedPrincipal user,
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

    public String getPrimitive() {
        return primitive;
    }

    public void setPrimitive(String primitive) {
        this.primitive = primitive;
    }

    public List<String> getXbtManifestIdList() {
        return xbtManifestIdList;
    }

    public void setXbtManifestIdList(List<String> xbtManifestIdList) {
        this.xbtManifestIdList = xbtManifestIdList;
    }
}
