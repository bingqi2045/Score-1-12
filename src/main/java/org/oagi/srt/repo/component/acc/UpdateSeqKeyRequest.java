package org.oagi.srt.repo.component.acc;

import org.oagi.srt.data.RepositoryRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcId;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateSeqKeyRequest extends RepositoryRequest {

    private final BigInteger accManifestId;
    private final Pair<CcId, CcId> itemAfterPair;

    public UpdateSeqKeyRequest(User user,
                               BigInteger accManifestId,
                               Pair<CcId, CcId> itemAfterPair) {
        super(user);
        this.accManifestId = accManifestId;
        this.itemAfterPair = itemAfterPair;
    }

    public UpdateSeqKeyRequest(User user,
                               LocalDateTime localDateTime,
                               BigInteger accManifestId,
                               Pair<CcId, CcId> itemAfterPair) {
        super(user, localDateTime);
        this.accManifestId = accManifestId;
        this.itemAfterPair = itemAfterPair;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }

    public CcId getItem() {
        return this.itemAfterPair.getFirst();
    }

    public CcId getAfter() {
        return this.itemAfterPair.getSecond();
    }
}
