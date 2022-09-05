package org.oagi.score.repo.api.corecomponent.seqkey.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

public class DeleteSeqKeyRequest extends Request {

    private String seqKeyId;

    public DeleteSeqKeyRequest(ScoreUser requester) {
        super(requester);
    }

    public String getSeqKeyId() {
        return seqKeyId;
    }

    public void setSeqKeyId(String seqKeyId) {
        this.seqKeyId = seqKeyId;
    }

    public DeleteSeqKeyRequest withSeqKeyId(String seqKeyId) {
        setSeqKeyId(seqKeyId);
        return this;
    }

}
