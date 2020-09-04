package org.oagi.score.repo.api.corecomponent.seqkey.model;

import org.oagi.score.repo.api.corecomponent.BccEntityType;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Iterator;

public class SeqKey implements Iterable<SeqKey>, Serializable {

    private BigInteger seqKeyId;

    private BigInteger fromAccId;

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

    public BigInteger getFromAccId() {
        return fromAccId;
    }

    public void setFromAccId(BigInteger fromAccId) {
        this.fromAccId = fromAccId;
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
        this.prevSeqKey = prevSeqKey;
    }

    public SeqKey getNextSeqKey() {
        return nextSeqKey;
    }

    public void setNextSeqKey(SeqKey nextSeqKey) {
        this.nextSeqKey = nextSeqKey;
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
