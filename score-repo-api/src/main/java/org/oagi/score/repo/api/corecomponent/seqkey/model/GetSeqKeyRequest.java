package org.oagi.score.repo.api.corecomponent.seqkey.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

public class GetSeqKeyRequest extends Request {

    private String seqKeyId;

    private String fromAccManifestId;

    public GetSeqKeyRequest(ScoreUser requester) {
        super(requester);
    }

    public String getSeqKeyId() {
        return seqKeyId;
    }

    public void setSeqKeyId(String seqKeyId) {
        this.seqKeyId = seqKeyId;
    }

    public GetSeqKeyRequest withSeqKeyId(String seqKeyId) {
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
