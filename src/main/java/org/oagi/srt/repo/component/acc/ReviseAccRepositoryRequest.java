package org.oagi.srt.repo.component.acc;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;

public class ReviseAccRepositoryRequest extends RepositoryRequest {

    private final BigInteger accManifestId;

    public ReviseAccRepositoryRequest(AuthenticatedPrincipal user,
                                      BigInteger accManifestId) {
        super(user);
        this.accManifestId = accManifestId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }
}
