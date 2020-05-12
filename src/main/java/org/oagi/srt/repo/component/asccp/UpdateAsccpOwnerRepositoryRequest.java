package org.oagi.srt.repo.component.asccp;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;

public class UpdateAsccpOwnerRepositoryRequest extends RepositoryRequest {

    private final BigInteger asccpManifestId;
    private final BigInteger ownerId;

    public UpdateAsccpOwnerRepositoryRequest(User user,
                                             BigInteger asccpManifestId,
                                             BigInteger ownerId) {
        super(user);
        this.asccpManifestId = asccpManifestId;
        this.ownerId = ownerId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }

    public BigInteger getOwnerId() {
        return ownerId;
    }
}
