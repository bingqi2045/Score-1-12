package org.oagi.score.repo.component.code_list;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class RestoreCodeListRepositoryRequest extends RepositoryRequest {

    private final String codeListManifestId;

    public RestoreCodeListRepositoryRequest(AuthenticatedPrincipal user,
                                            String codeListManifestId) {
        super(user);
        this.codeListManifestId = codeListManifestId;
    }

    public RestoreCodeListRepositoryRequest(AuthenticatedPrincipal user,
                                            LocalDateTime localDateTime,
                                            String codeListManifestId) {
        super(user, localDateTime);
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
