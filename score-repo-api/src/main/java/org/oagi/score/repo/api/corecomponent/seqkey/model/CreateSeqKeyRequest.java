package org.oagi.score.repo.api.corecomponent.seqkey.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

public class CreateSeqKeyRequest extends Request {

    private String fromAccManifestId;

    private SeqKeyType type;

    private String manifestId;

    public CreateSeqKeyRequest(ScoreUser requester) {
        super(requester);
    }

    public String getFromAccManifestId() {
        return fromAccManifestId;
    }

    public void setFromAccManifestId(String fromAccManifestId) {
        this.fromAccManifestId = fromAccManifestId;
    }

    public CreateSeqKeyRequest withFromAccManifestId(String fromAccManifestId) {
        setFromAccManifestId(fromAccManifestId);
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

    public String getManifestId() {
        return manifestId;
    }

    public void setManifestId(String manifestId) {
        this.manifestId = manifestId;
    }

    public CreateSeqKeyRequest withManifestId(String manifestId) {
        setManifestId(manifestId);
        return this;
    }

}
