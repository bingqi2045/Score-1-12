package org.oagi.srt.repo.component.acc;

import org.oagi.srt.data.RepositoryRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcId;
import org.oagi.srt.gateway.http.api.cc_management.data.CcSeqUpdateRequest;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public class UpdateSeqKeyRequest extends RepositoryRequest {

    private final BigInteger accManifestId;
    private final CcSeqUpdateRequest seqUpdateRequest;

    public UpdateSeqKeyRequest(User user,
                               BigInteger accManifestId,
                               CcSeqUpdateRequest seqUpdateRequest) {
        super(user);
        this.accManifestId = accManifestId;
        this.seqUpdateRequest = seqUpdateRequest;
    }

    public UpdateSeqKeyRequest(User user,
                               LocalDateTime localDateTime,
                               BigInteger accManifestId,
                               CcSeqUpdateRequest seqUpdateRequest) {
        super(user, localDateTime);
        this.accManifestId = accManifestId;
        this.seqUpdateRequest = seqUpdateRequest;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }

    public CcSeqUpdateRequest getSeqUpdateRequest() {
        return seqUpdateRequest;
    }
}
