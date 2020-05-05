package org.oagi.srt.repo.component.bccp;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;

public class ReviseBccpRepositoryRequest extends RepositoryRequest {

    private final BigInteger bccpManifestId;

    public ReviseBccpRepositoryRequest(User user,
                                       BigInteger bccpManifestId) {
        super(user);
        this.bccpManifestId = bccpManifestId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }
}
