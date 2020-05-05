package org.oagi.srt.repo.component.acc;

import org.oagi.srt.data.RepositoryRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateAccStateRepositoryRequest extends RepositoryRequest {

    private final BigInteger accManifestId;
    private final CcState state;

    public UpdateAccStateRepositoryRequest(User user,
                                           BigInteger accManifestId,
                                           CcState state) {
        super(user);
        this.accManifestId = accManifestId;
        this.state = state;
    }

    public UpdateAccStateRepositoryRequest(User user,
                                           LocalDateTime localDateTime,
                                           BigInteger accManifestId,
                                           CcState state) {
        super(user, localDateTime);
        this.accManifestId = accManifestId;
        this.state = state;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }

    public CcState getState() {
        return state;
    }
}
