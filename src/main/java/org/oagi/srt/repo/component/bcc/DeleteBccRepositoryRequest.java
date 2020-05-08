package org.oagi.srt.repo.component.bcc;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class DeleteBccRepositoryRequest extends RepositoryRequest {

    private final BigInteger bccManifestId;

    public DeleteBccRepositoryRequest(User user,
                                      BigInteger bccManifestId) {
        super(user);
        this.bccManifestId = bccManifestId;
    }

    public DeleteBccRepositoryRequest(User user,
                                      LocalDateTime localDateTime,
                                      BigInteger bccManifestId) {
        super(user, localDateTime);
        this.bccManifestId = bccManifestId;
    }

    public BigInteger getBccManifestId() {
        return bccManifestId;
    }
}
