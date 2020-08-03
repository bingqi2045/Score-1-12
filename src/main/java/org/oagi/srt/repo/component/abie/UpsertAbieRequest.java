package org.oagi.srt.repo.component.abie;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpsertAbieRequest extends RepositoryRequest {

    private final BigInteger topLevelAsbiepId;
    private final AbieNode.Abie abie;

    public UpsertAbieRequest(User user, LocalDateTime localDateTime,
                             BigInteger topLevelAsbiepId, AbieNode.Abie abie) {
        super(user, localDateTime);
        this.topLevelAsbiepId = topLevelAsbiepId;
        this.abie = abie;
    }

    public BigInteger getTopLevelAsbiepId() {
        return topLevelAsbiepId;
    }

    public AbieNode.Abie getAbie() {
        return abie;
    }
}
