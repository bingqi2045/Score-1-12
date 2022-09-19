package org.oagi.score.repo.component.code_list;

import org.oagi.score.data.RepositoryRequest;
import org.oagi.score.service.common.data.CcState;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class UpdateCodeListStateRepositoryRequest extends RepositoryRequest {

    private final String codeListManifestId;
    private final CcState state;

    public UpdateCodeListStateRepositoryRequest(AuthenticatedPrincipal user,
                                                String codeListManifestId,
                                                CcState state) {
        super(user);
        this.codeListManifestId = codeListManifestId;
        this.state = state;
    }

    public UpdateCodeListStateRepositoryRequest(AuthenticatedPrincipal user,
                                                LocalDateTime localDateTime,
                                                String codeListManifestId,
                                                CcState state) {
        super(user, localDateTime);
        this.codeListManifestId = codeListManifestId;
        this.state = state;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }

    public CcState getState() {
        return state;
    }
}
