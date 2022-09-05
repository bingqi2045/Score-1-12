package org.oagi.score.repo.component.code_list;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class ReviseCodeListRepositoryRequest extends RepositoryRequest {

    private final String codeListManifestId;

    public ReviseCodeListRepositoryRequest(AuthenticatedPrincipal user,
                                           String codeListManifestId,
                                           LocalDateTime localDateTime) {
        super(user, localDateTime);
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
