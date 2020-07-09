package org.oagi.srt.repo.component.asccp;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;

public class DiscardRevisionAsccpRepositoryRequest extends RepositoryRequest {

    private final BigInteger asccpManifestId;

    public DiscardRevisionAsccpRepositoryRequest(User user,
                                                 BigInteger asccpManifestId) {
        super(user);
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }
}
