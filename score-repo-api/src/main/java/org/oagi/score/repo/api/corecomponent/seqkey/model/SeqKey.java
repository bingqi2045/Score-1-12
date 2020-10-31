package org.oagi.score.repo.api.corecomponent.seqkey.model;

import org.oagi.score.repo.api.corecomponent.BccEntityType;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Objects;

public class SeqKey implements Iterable<SeqKey>, Serializable {

    private BigInteger seqKeyId;

    private BigInteger fromAccManifestId;

    private SeqKeyType seqKeyType;

    private BigInteger ccId;

    private BccEntityType entityType;

    private SeqKey prevSeqKey;

    private SeqKey nextSeqKey;

    public BigInteger getSeqKeyId() {
        return seqKeyId;
    }

    public void setSeqKeyId(BigInteger seqKeyId) {
        this.seqKeyId = seqKeyId;
    }

    public BigInteger getFromAccManifestId() {
        return fromAccManifestId;
    }

    public void setFromAccManifestId(BigInteger fromAccManifestId) {
        this.fromAccManifestId = fromAccManifestId;
    }

    public SeqKeyType getSeqKeyType() {
        return seqKeyType;
    }

    public void setSeqKeyType(SeqKeyType seqKeyType) {
        this.seqKeyType = seqKeyType;
    }

    public BigInteger getCcId() {
        return ccId;
    }

    public void setCcId(BigInteger ccId) {
        this.ccId = ccId;
    }

    public BccEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(BccEntityType entityType) {
        this.entityType = entityType;
    }

    public SeqKey getPrevSeqKey() {
        return prevSeqKey;
    }

    public void setPrevSeqKey(SeqKey prevSeqKey) {
        if (prevSeqKey != null && prevSeqKey.equals(this.prevSeqKey)) {
            return;
        }

        this.prevSeqKey = prevSeqKey;
        if (prevSeqKey != null) {
            prevSeqKey.setNextSeqKey(this);
        }

        if (this.nextSeqKey != null && this.nextSeqKey.equals(this.prevSeqKey)) {
            throw new IllegalStateException();
        }
    }

    public SeqKey getNextSeqKey() {
        return nextSeqKey;
    }

    public void setNextSeqKey(SeqKey nextSeqKey) {
        if (nextSeqKey != null && nextSeqKey.equals(this.nextSeqKey)) {
            return;
        }

        this.nextSeqKey = nextSeqKey;
        if (nextSeqKey != null) {
            nextSeqKey.setPrevSeqKey(this);
        }

        if (this.prevSeqKey != null && this.prevSeqKey.equals(this.nextSeqKey)) {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeqKey seqKey = (SeqKey) o;
        return fromAccManifestId.equals(seqKey.fromAccManifestId) &&
                seqKeyType == seqKey.seqKeyType &&
                ccId.equals(seqKey.ccId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromAccManifestId, seqKeyType, ccId);
    }

    @Override
    public String toString() {
        return "SeqKey{" +
                "seqKeyId=" + seqKeyId +
                ", prevSeqKey=" + ((prevSeqKey != null) ? prevSeqKey.getSeqKeyId() : null) +
                ", nextSeqKey=" + ((nextSeqKey != null) ? nextSeqKey.getSeqKeyId() : null) +
                ", fromAccId=" + fromAccManifestId +
                ", seqKeyType=" + seqKeyType +
                ", ccId=" + ccId +
                '}';
    }

    @Override
    public Iterator<SeqKey> iterator() {
        return new SeqKeyIterator(this);
    }

    private class SeqKeyIterator implements Iterator<SeqKey> {

        private SeqKey seqKey;

        public SeqKeyIterator(SeqKey seqKey) {
            this.seqKey = seqKey;
        }

        @Override
        public boolean hasNext() {
            return (this.seqKey != null);
        }

        @Override
        public SeqKey next() {
            SeqKey thisSeqKey = this.seqKey;
            this.seqKey = this.seqKey.getNextSeqKey();
            return thisSeqKey;
        }
    }
}
