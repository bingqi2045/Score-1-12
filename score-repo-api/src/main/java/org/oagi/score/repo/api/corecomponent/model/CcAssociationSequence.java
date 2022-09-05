package org.oagi.score.repo.api.corecomponent.model;

import java.math.BigInteger;

public class CcAssociationSequence {

    private BigInteger seqKeyId;

    private String fromAccManifestId;

    private String asccManifestId;

    private String bccManifestId;

    private BigInteger prevSeqKeyId;

    private BigInteger nextSeqKeyId;

    public BigInteger getSeqKeyId() {
        return seqKeyId;
    }

    public void setSeqKeyId(BigInteger seqKeyId) {
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

    public BigInteger getPrevSeqKeyId() {
        return prevSeqKeyId;
    }

    public void setPrevSeqKeyId(BigInteger prevSeqKeyId) {
        this.prevSeqKeyId = prevSeqKeyId;
    }

    public BigInteger getNextSeqKeyId() {
        return nextSeqKeyId;
    }

    public void setNextSeqKeyId(BigInteger nextSeqKeyId) {
        this.nextSeqKeyId = nextSeqKeyId;
    }
}
