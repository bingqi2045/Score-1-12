package org.oagi.srt.repo.component.bccp;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateBccpBdtRepositoryRequest extends RepositoryRequest {

    private final BigInteger bccpManifestId;
    private final BigInteger bdtManifestId;

    public UpdateBccpBdtRepositoryRequest(User user,
                                          BigInteger bccpManifestId,
                                          BigInteger bdtManifestId) {
        super(user);
        this.bccpManifestId = bccpManifestId;
        this.bdtManifestId = bdtManifestId;
    }

    public UpdateBccpBdtRepositoryRequest(User user,
                                          LocalDateTime localDateTime,
                                          BigInteger bccpManifestId,
                                          BigInteger bdtManifestId) {
        super(user, localDateTime);
        this.bccpManifestId = bccpManifestId;
        this.bdtManifestId = bdtManifestId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }

    public BigInteger getBdtManifestId() {
        return bdtManifestId;
    }
}
