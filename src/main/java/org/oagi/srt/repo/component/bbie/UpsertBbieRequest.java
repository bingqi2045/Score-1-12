package org.oagi.srt.repo.component.bbie;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpsertBbieRequest extends RepositoryRequest {

    private final BigInteger topLevelAbieId;
    private final BbieNode.Bbie bbie;

    public UpsertBbieRequest(User user, LocalDateTime localDateTime,
                             BigInteger topLevelAbieId, BbieNode.Bbie bbie) {
        super(user, localDateTime);
        this.topLevelAbieId = topLevelAbieId;
        this.bbie = bbie;
    }

    public BigInteger getTopLevelAbieId() {
        return topLevelAbieId;
    }

    public BbieNode.Bbie getBbie() {
        return bbie;
    }
}
