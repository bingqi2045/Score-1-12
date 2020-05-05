package org.oagi.srt.repo.component.asccp;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class DeleteAsccpRepositoryRequest extends RepositoryRequest {

    private final BigInteger asccpManifestId;

    public DeleteAsccpRepositoryRequest(User user,
                                        BigInteger asccpManifestId) {
        super(user);
        this.asccpManifestId = asccpManifestId;
    }

    public DeleteAsccpRepositoryRequest(User user,
                                        LocalDateTime localDateTime,
                                        BigInteger asccpManifestId) {
        super(user, localDateTime);
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }
}
