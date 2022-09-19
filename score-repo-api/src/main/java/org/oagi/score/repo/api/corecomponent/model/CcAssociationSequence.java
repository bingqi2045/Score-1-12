package org.oagi.score.repo.api.corecomponent.model;

public class CcAssociationSequence {

    private String seqKeyId;

    private String fromAccManifestId;

    private String asccManifestId;

    private String bccManifestId;

    private String prevSeqKeyId;

    private String nextSeqKeyId;

    public String getSeqKeyId() {
        return seqKeyId;
    }

    public void setSeqKeyId(String seqKeyId) {
        this.seqKeyId = seqKeyId;
    }

    public String getFromAccManifestId() {
        return fromAccManifestId;
    }

    public void setFromAccManifestId(String fromAccManifestId) {
        this.fromAccManifestId = fromAccManifestId;
    }

    public String getAsccManifestId() {
        return asccManifestId;
    }

    public void setAsccManifestId(String asccManifestId) {
        this.asccManifestId = asccManifestId;
    }

    public String getBccManifestId() {
        return bccManifestId;
    }

    public void setBccManifestId(String bccManifestId) {
        this.bccManifestId = bccManifestId;
    }

    public String getPrevSeqKeyId() {
        return prevSeqKeyId;
    }

    public void setPrevSeqKeyId(String prevSeqKeyId) {
        this.prevSeqKeyId = prevSeqKeyId;
    }

    public String getNextSeqKeyId() {
        return nextSeqKeyId;
    }

    public void setNextSeqKeyId(String nextSeqKeyId) {
        this.nextSeqKeyId = nextSeqKeyId;
    }
}
