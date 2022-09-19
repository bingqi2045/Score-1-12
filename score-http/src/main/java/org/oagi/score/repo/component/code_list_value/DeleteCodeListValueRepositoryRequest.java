package org.oagi.score.repo.component.code_list_value;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class DeleteCodeListValueRepositoryRequest extends RepositoryRequest {

    private final String codeListValueManifestId;

    public DeleteCodeListValueRepositoryRequest(AuthenticatedPrincipal user,
                                                String codeListValueManifestId) {
        super(user);
        this.codeListValueManifestId = codeListValueManifestId;
    }

    public DeleteCodeListValueRepositoryRequest(AuthenticatedPrincipal user,
                                                LocalDateTime localDateTime,
                                                String codeListValueManifestId) {
        super(user, localDateTime);
        this.codeListValueManifestId = codeListValueManifestId;
    }

    public String getCodeListValueManifestId() {
        return codeListValueManifestId;
    }
}
