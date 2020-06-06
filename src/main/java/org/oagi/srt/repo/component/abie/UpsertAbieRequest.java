package org.oagi.srt.repo.component.abie;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpsertAbieRequest extends RepositoryRequest {

    private final BigInteger topLevelAbieId;
    private final AbieNode.Abie abie;

    public UpsertAbieRequest(User user, LocalDateTime localDateTime,
                             BigInteger topLevelAbieId, AbieNode.Abie abie) {
        super(user, localDateTime);
        this.topLevelAbieId = topLevelAbieId;
        this.abie = abie;
    }

    public BigInteger getTopLevelAbieId() {
        return topLevelAbieId;
    }

    public AbieNode.Abie getAbie() {
        return abie;
    }
}
