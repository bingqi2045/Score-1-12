package org.oagi.score.repo.component.code_list;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;

public class UpdateCodeListOwnerRepositoryRequest extends RepositoryRequest {

    private final String codeListManifestId;
    private final String ownerId;

    public UpdateCodeListOwnerRepositoryRequest(AuthenticatedPrincipal user,
                                                String codeListManifestId,
                                                String ownerId) {
        super(user);
        this.codeListManifestId = codeListManifestId;
        this.ownerId = ownerId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }

    public String getOwnerId() {
        return ownerId;
    }
}
