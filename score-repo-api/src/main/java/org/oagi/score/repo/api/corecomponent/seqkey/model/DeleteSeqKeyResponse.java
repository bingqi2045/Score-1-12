package org.oagi.score.repo.api.corecomponent.seqkey.model;

import org.oagi.score.repo.api.base.Response;

public class DeleteSeqKeyResponse extends Response {

    private final String seqKeyId;

    public DeleteSeqKeyResponse(String seqKeyId) {
        this.seqKeyId = seqKeyId;
    }

    public String getSeqKeyId() {
        return seqKeyId;
    }

}
