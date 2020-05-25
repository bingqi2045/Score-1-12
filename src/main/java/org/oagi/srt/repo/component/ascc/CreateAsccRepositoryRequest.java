package org.oagi.srt.repo.component.ascc;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class CreateAsccRepositoryRequest extends RepositoryRequest {

    private final BigInteger releaseId;
    private final BigInteger accManifestId;
    private final BigInteger asccpManifestId;
    private int pos = -1;

    public CreateAsccRepositoryRequest(User user,
                                       BigInteger releaseId,
                                       BigInteger accManifestId,
                                       BigInteger asccpManifestId) {
        super(user);
        this.releaseId = releaseId;
        this.accManifestId = accManifestId;
        this.asccpManifestId = asccpManifestId;
    }

    public CreateAsccRepositoryRequest(User user,
                                       LocalDateTime localDateTime,
                                       BigInteger releaseId,
                                       BigInteger accManifestId,
                                       BigInteger asccpManifestId) {
        super(user, localDateTime);
        this.releaseId = releaseId;
        this.accManifestId = accManifestId;
        this.asccpManifestId = asccpManifestId;
    }

    public BigInteger getReleaseId() {
        return releaseId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
