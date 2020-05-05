package org.oagi.srt.repo.component.acc;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateAccBasedAccRepositoryRequest extends RepositoryRequest {

    private final BigInteger accManifestId;
    private final BigInteger basedAccManifestId;

    public UpdateAccBasedAccRepositoryRequest(User user,
                                              BigInteger accManifestId,
                                              BigInteger basedAccManifestId) {
        super(user);
        this.accManifestId = accManifestId;
        this.basedAccManifestId = basedAccManifestId;
    }

    public UpdateAccBasedAccRepositoryRequest(User user,
                                              LocalDateTime localDateTime,
                                              BigInteger accManifestId,
                                              BigInteger basedAccManifestId) {
        super(user, localDateTime);
        this.accManifestId = accManifestId;
        this.basedAccManifestId = basedAccManifestId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }

    public BigInteger getBasedAccManifestId() {
        return basedAccManifestId;
    }
}
