package org.oagi.srt.repo.component.asbiep;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpsertAsbiepRequest extends RepositoryRequest {

    private final BigInteger topLevelAbieId;
    private final AsbiepNode.Asbiep asbiep;
    private BigInteger refTopLevelAbieId;

    public UpsertAsbiepRequest(User user, LocalDateTime localDateTime,
                               BigInteger topLevelAbieId, AsbiepNode.Asbiep asbiep) {
        super(user, localDateTime);
        this.topLevelAbieId = topLevelAbieId;
        this.asbiep = asbiep;
    }

    public BigInteger getTopLevelAbieId() {
        return topLevelAbieId;
    }

    public AsbiepNode.Asbiep getAsbiep() {
        return asbiep;
    }

    public BigInteger getRefTopLevelAbieId() {
        return refTopLevelAbieId;
    }

    public void setRefTopLevelAbieId(BigInteger refTopLevelAbieId) {
        this.refTopLevelAbieId = refTopLevelAbieId;
    }
}
