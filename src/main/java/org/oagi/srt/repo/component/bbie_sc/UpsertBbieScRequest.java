package org.oagi.srt.repo.component.bbie_sc;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpsertBbieScRequest extends RepositoryRequest {

    private final BigInteger topLevelAbieId;
    private final BbieScNode.BbieSc bbieSc;

    public UpsertBbieScRequest(User user, LocalDateTime localDateTime,
                               BigInteger topLevelAbieId, BbieScNode.BbieSc bbieSc) {
        super(user, localDateTime);
        this.topLevelAbieId = topLevelAbieId;
        this.bbieSc = bbieSc;
    }

    public BigInteger getTopLevelAbieId() {
        return topLevelAbieId;
    }

    public BbieScNode.BbieSc getBbieSc() {
        return bbieSc;
    }
}
