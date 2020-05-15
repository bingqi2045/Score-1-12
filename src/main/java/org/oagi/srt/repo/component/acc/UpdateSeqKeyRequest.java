package org.oagi.srt.repo.component.acc;

import org.oagi.srt.data.RepositoryRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcId;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public class UpdateSeqKeyRequest extends RepositoryRequest {

    private final BigInteger accManifestId;
    private final List<Pair<CcId, CcId>> itemAfterPairs;

    public UpdateSeqKeyRequest(User user,
                               BigInteger accManifestId,
                               List<Pair<CcId, CcId>> itemAfterPairs) {
        super(user);
        this.accManifestId = accManifestId;
        this.itemAfterPairs = itemAfterPairs;
    }

    public UpdateSeqKeyRequest(User user,
                               LocalDateTime localDateTime,
                               BigInteger accManifestId,
                               List<Pair<CcId, CcId>> itemAfterPairs) {
        super(user, localDateTime);
        this.accManifestId = accManifestId;
        this.itemAfterPairs = itemAfterPairs;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }

    public List<Pair<CcId, CcId>> getItemAfterPairs() {
        return itemAfterPairs;
    }
}
