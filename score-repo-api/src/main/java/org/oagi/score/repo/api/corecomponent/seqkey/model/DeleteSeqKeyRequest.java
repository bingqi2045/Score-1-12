package org.oagi.score.repo.api.corecomponent.seqkey.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

public class DeleteSeqKeyRequest extends Request {

    private SeqKey seqKey;

    public DeleteSeqKeyRequest(ScoreUser requester) {
        super(requester);
    }

    public SeqKey getSeqKey() {
        return seqKey;
    }

    public void setSeqKey(SeqKey seqKey) {
        this.seqKey = seqKey;
    }

    public DeleteSeqKeyRequest withSeqKey(SeqKey seqKey) {
        setSeqKey(seqKey);
        return this;
    }

}
