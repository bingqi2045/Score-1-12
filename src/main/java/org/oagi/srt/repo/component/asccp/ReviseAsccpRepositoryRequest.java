package org.oagi.srt.repo.component.asccp;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;

public class ReviseAsccpRepositoryRequest extends RepositoryRequest {

    private final BigInteger asccpManifestId;

    public ReviseAsccpRepositoryRequest(User user,
                                        BigInteger asccpManifestId) {
        super(user);
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }
}
