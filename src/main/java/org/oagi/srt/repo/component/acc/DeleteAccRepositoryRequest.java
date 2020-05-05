package org.oagi.srt.repo.component.acc;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class DeleteAccRepositoryRequest extends RepositoryRequest {

    private final BigInteger accManifestId;

    public DeleteAccRepositoryRequest(User user,
                                      BigInteger accManifestId) {
        super(user);
        this.accManifestId = accManifestId;
    }

    public DeleteAccRepositoryRequest(User user,
                                      LocalDateTime localDateTime,
                                      BigInteger accManifestId) {
        super(user, localDateTime);
        this.accManifestId = accManifestId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }
}
