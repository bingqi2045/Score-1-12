package org.oagi.score.repo.api.corecomponent.seqkey.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class GetSeqKeyRequest extends Request {

    private BigInteger seqKeyId;

    private String fromAccManifestId;

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

    public String getFromAccManifestId() {
        return fromAccManifestId;
    }

    public void setFromAccManifestId(String fromAccManifestId) {
        this.fromAccManifestId = fromAccManifestId;
    }

    public GetSeqKeyRequest withFromAccManifestId(String fromAccManifestId) {
        setFromAccManifestId(fromAccManifestId);
        return this;
    }

}
