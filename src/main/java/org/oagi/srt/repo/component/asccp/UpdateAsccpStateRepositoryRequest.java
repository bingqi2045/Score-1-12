package org.oagi.srt.repo.component.asccp;

import org.oagi.srt.data.RepositoryRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateAsccpStateRepositoryRequest extends RepositoryRequest {

    private final BigInteger asccpManifestId;
    private final CcState state;

    public UpdateAsccpStateRepositoryRequest(User user,
                                             BigInteger asccpManifestId,
                                             CcState state) {
        super(user);
        this.asccpManifestId = asccpManifestId;
        this.state = state;
    }

    public UpdateAsccpStateRepositoryRequest(User user,
                                             LocalDateTime localDateTime,
                                             BigInteger asccpManifestId,
                                             CcState state) {
        super(user, localDateTime);
        this.asccpManifestId = asccpManifestId;
        this.state = state;
    }

    public BigInteger getAsccpManifestId() {
        return asccpManifestId;
    }

    public CcState getState() {
        return state;
    }
}
