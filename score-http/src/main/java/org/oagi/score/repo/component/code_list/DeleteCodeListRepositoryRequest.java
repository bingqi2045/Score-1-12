package org.oagi.score.repo.component.code_list;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class DeleteCodeListRepositoryRequest extends RepositoryRequest {

    private final String codeListManifestId;

    public DeleteCodeListRepositoryRequest(AuthenticatedPrincipal user,
                                           String codeListManifestId) {
        super(user);
        this.codeListManifestId = codeListManifestId;
    }

    public DeleteCodeListRepositoryRequest(AuthenticatedPrincipal user,
                                           LocalDateTime localDateTime,
                                           String codeListManifestId) {
        super(user, localDateTime);
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
