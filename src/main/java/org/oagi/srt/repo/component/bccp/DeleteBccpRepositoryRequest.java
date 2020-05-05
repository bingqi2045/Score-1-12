package org.oagi.srt.repo.component.bccp;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class DeleteBccpRepositoryRequest extends RepositoryRequest {

    private final BigInteger bccpManifestId;

    public DeleteBccpRepositoryRequest(User user,
                                       BigInteger bccpManifestId) {
        super(user);
        this.bccpManifestId = bccpManifestId;
    }

    public DeleteBccpRepositoryRequest(User user,
                                       LocalDateTime localDateTime,
                                       BigInteger bccpManifestId) {
        super(user, localDateTime);
        this.bccpManifestId = bccpManifestId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }
}
