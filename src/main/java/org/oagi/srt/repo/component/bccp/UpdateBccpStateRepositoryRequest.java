package org.oagi.srt.repo.component.bccp;

import org.oagi.srt.data.RepositoryRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateBccpStateRepositoryRequest extends RepositoryRequest {

    private final BigInteger bccpManifestId;
    private final CcState state;

    public UpdateBccpStateRepositoryRequest(User user,
                                            BigInteger bccpManifestId,
                                            CcState state) {
        super(user);
        this.bccpManifestId = bccpManifestId;
        this.state = state;
    }

    public UpdateBccpStateRepositoryRequest(User user,
                                            LocalDateTime localDateTime,
                                            BigInteger bccpManifestId,
                                            CcState state) {
        super(user, localDateTime);
        this.bccpManifestId = bccpManifestId;
        this.state = state;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }

    public CcState getState() {
        return state;
    }
}
