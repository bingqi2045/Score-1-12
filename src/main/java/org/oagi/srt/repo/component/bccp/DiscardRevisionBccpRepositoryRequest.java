package org.oagi.srt.repo.component.bccp;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;

public class DiscardRevisionBccpRepositoryRequest extends RepositoryRequest {

    private final BigInteger bccpManifestId;

    public DiscardRevisionBccpRepositoryRequest(User user,
                                                BigInteger bccpManifestId) {
        super(user);
        this.bccpManifestId = bccpManifestId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }
}
