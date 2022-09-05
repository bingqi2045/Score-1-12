package org.oagi.score.repo.component.asccp;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateAsccpRoleOfAccRepositoryRequest extends RepositoryRequest {

    private final String asccpManifestId;
    private final String roleOfAccManifestId;

    public UpdateAsccpRoleOfAccRepositoryRequest(AuthenticatedPrincipal user,
                                                 String asccpManifestId,
                                                 String roleOfAccManifestId) {
        super(user);
        this.asccpManifestId = asccpManifestId;
        this.roleOfAccManifestId = roleOfAccManifestId;
    }

    public UpdateAsccpRoleOfAccRepositoryRequest(AuthenticatedPrincipal user,
                                                 LocalDateTime localDateTime,
                                                 String asccpManifestId,
                                                 String roleOfAccManifestId) {
        super(user, localDateTime);
        this.asccpManifestId = asccpManifestId;
        this.roleOfAccManifestId = roleOfAccManifestId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }

    public String getRoleOfAccManifestId() {
        return roleOfAccManifestId;
    }
}
