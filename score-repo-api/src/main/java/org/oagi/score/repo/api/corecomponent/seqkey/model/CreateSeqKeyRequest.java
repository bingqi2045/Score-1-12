package org.oagi.score.repo.api.corecomponent.seqkey.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class CreateSeqKeyRequest extends Request {

    private BigInteger fromAccId;

    private SeqKeyType type;

    private BigInteger ccId;

    public CreateSeqKeyRequest(ScoreUser requester) {
        super(requester);
    }

    public BigInteger getFromAccId() {
        return fromAccId;
    }

    public void setFromAccId(BigInteger fromAccId) {
        this.fromAccId = fromAccId;
    }

    public CreateSeqKeyRequest withFromAccId(BigInteger fromAccId) {
        setFromAccId(fromAccId);
        return this;
    }

    public SeqKeyType getType() {
        return type;
    }

    public void setType(SeqKeyType type) {
        this.type = type;
    }

    public CreateSeqKeyRequest withType(SeqKeyType type) {
        setType(type);
        return this;
    }

    public BigInteger getCcId() {
        return ccId;
    }

    public void setCcId(BigInteger ccId) {
        this.ccId = ccId;
    }

    public CreateSeqKeyRequest withCcId(BigInteger ccId) {
        setCcId(ccId);
        return this;
    }

}
