package org.oagi.srt.repo.component.release;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class ReleaseRepositoryDiscardRequest extends RepositoryRequest {

    private final BigInteger releaseId;

    public ReleaseRepositoryDiscardRequest(User user, BigInteger releaseId) {
        super(user);
        this.releaseId = releaseId;
    }

    public ReleaseRepositoryDiscardRequest(User user,
                                           LocalDateTime localDateTime,
                                           BigInteger releaseId) {
        super(user, localDateTime);
        this.releaseId = releaseId;
    }

    public BigInteger getReleaseId() {
        return releaseId;
    }
}
