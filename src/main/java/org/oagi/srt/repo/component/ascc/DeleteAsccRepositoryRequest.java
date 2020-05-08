package org.oagi.srt.repo.component.ascc;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class DeleteAsccRepositoryRequest extends RepositoryRequest {

    private final BigInteger asccManifestId;

    public DeleteAsccRepositoryRequest(User user,
                                       BigInteger asccManifestId) {
        super(user);
        this.asccManifestId = asccManifestId;
    }

    public DeleteAsccRepositoryRequest(User user,
                                       LocalDateTime localDateTime,
                                       BigInteger asccManifestId) {
        super(user, localDateTime);
        this.asccManifestId = asccManifestId;
    }

    public BigInteger getAsccManifestId() {
        return asccManifestId;
    }
}
