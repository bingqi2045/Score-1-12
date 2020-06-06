package org.oagi.srt.repo.component.asbie;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpsertAsbieRequest extends RepositoryRequest {

    private final BigInteger topLevelAbieId;
    private final AsbieNode.Asbie asbie;

    public UpsertAsbieRequest(User user, LocalDateTime localDateTime,
                              BigInteger topLevelAbieId, AsbieNode.Asbie asbie) {
        super(user, localDateTime);
        this.topLevelAbieId = topLevelAbieId;
        this.asbie = asbie;
    }

    public BigInteger getTopLevelAbieId() {
        return topLevelAbieId;
    }

    public AsbieNode.Asbie getAsbie() {
        return asbie;
    }
}
