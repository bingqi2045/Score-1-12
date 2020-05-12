package org.oagi.srt.repo.component.bccp;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;

public class UpdateBccpOwnerRepositoryRequest extends RepositoryRequest {

    private final BigInteger bccpManifestId;
    private final BigInteger ownerId;

    public UpdateBccpOwnerRepositoryRequest(User user,
                                            BigInteger bccpManifestId,
                                            BigInteger ownerId) {
        super(user);
        this.bccpManifestId = bccpManifestId;
        this.ownerId = ownerId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }

    public BigInteger getOwnerId() {
        return ownerId;
    }
}
