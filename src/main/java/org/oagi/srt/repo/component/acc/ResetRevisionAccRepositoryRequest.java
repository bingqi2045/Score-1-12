package org.oagi.srt.repo.component.acc;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;

public class ResetRevisionAccRepositoryRequest extends RepositoryRequest {

    private final BigInteger accManifestId;

    public ResetRevisionAccRepositoryRequest(User user,
                                             BigInteger accManifestId) {
        super(user);
        this.accManifestId = accManifestId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }
}
