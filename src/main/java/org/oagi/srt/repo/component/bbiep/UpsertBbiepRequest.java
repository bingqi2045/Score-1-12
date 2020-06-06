package org.oagi.srt.repo.component.bbiep;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpsertBbiepRequest extends RepositoryRequest {

    private final BigInteger topLevelAbieId;
    private final BbiepNode.Bbiep bbiep;

    public UpsertBbiepRequest(User user, LocalDateTime localDateTime,
                              BigInteger topLevelAbieId, BbiepNode.Bbiep bbiep) {
        super(user, localDateTime);
        this.topLevelAbieId = topLevelAbieId;
        this.bbiep = bbiep;
    }

    public BigInteger getTopLevelAbieId() {
        return topLevelAbieId;
    }

    public BbiepNode.Bbiep getBbiep() {
        return bbiep;
    }
}
