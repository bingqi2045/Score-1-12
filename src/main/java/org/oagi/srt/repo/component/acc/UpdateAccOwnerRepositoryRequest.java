package org.oagi.srt.repo.component.acc;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateAccOwnerRepositoryRequest extends RepositoryRequest {

    private final BigInteger accManifestId;
    private final BigInteger ownerId;

    public UpdateAccOwnerRepositoryRequest(User user,
                                           BigInteger accManifestId,
                                           BigInteger ownerId) {
        super(user);
        this.accManifestId = accManifestId;
        this.ownerId = ownerId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }

    public BigInteger getOwnerId() {
        return ownerId;
    }
}
