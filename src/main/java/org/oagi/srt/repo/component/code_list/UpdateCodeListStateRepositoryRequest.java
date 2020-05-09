package org.oagi.srt.repo.component.code_list;

import org.oagi.srt.data.RepositoryRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateCodeListStateRepositoryRequest extends RepositoryRequest {

    private final BigInteger codeListManifestId;
    private final CcState state;

    public UpdateCodeListStateRepositoryRequest(User user,
                                                BigInteger codeListManifestId,
                                                CcState state) {
        super(user);
        this.codeListManifestId = codeListManifestId;
        this.state = state;
    }

    public UpdateCodeListStateRepositoryRequest(User user,
                                                LocalDateTime localDateTime,
                                                BigInteger codeListManifestId,
                                                CcState state) {
        super(user, localDateTime);
        this.codeListManifestId = codeListManifestId;
        this.state = state;
    }

    public BigInteger getCodeListManifestId() {
        return codeListManifestId;
    }

    public CcState getState() {
        return state;
    }
}
