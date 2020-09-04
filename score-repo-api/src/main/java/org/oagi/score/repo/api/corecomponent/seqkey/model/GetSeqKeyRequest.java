package org.oagi.score.repo.api.corecomponent.seqkey.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class GetSeqKeyRequest extends Request {

    private BigInteger seqKeyId;

    private BigInteger fromAccId;

    public GetSeqKeyRequest(ScoreUser requester) {
        super(requester);
    }

    public BigInteger getSeqKeyId() {
        return seqKeyId;
    }

    public void setSeqKeyId(BigInteger seqKeyId) {
        this.seqKeyId = seqKeyId;
    }

    public GetSeqKeyRequest withSeqKeyId(BigInteger seqKeyId) {
        setSeqKeyId(seqKeyId);
        return this;
    }

    public BigInteger getFromAccId() {
        return fromAccId;
    }

    public void setFromAccId(BigInteger fromAccId) {
        this.fromAccId = fromAccId;
    }

    public GetSeqKeyRequest withFromAccId(BigInteger fromAccId) {
        setFromAccId(fromAccId);
        return this;
    }

}
