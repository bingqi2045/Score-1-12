package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;
import java.util.Objects;

public class BccManifest implements CcManifest, CcAssociation, Serializable {

    private String bccManifestId;

    private String releaseId;

    private String bccId;

    private String seqKeyId;

    private String fromAccManifestId;

    private String toBccpManifestId;

    private boolean conflict;

    private String prevBccManifestId;

    private String nextBccManifestId;

    public String getBccManifestId() {
        return bccManifestId;
    }

    public void setBccManifestId(String bccManifestId) {
        this.bccManifestId = bccManifestId;
    }

    @Override
    public String getManifestId() {
        return getBccManifestId();
    }

    @Override
    public String getReleaseId() {
        return releaseId;
    }

    @Override
    public String getBasedCcId() {
        return getBccId();
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getBccId() {
        return bccId;
    }

    public void setBccId(String bccId) {
        this.bccId = bccId;
    }

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

    public String getToBccpManifestId() {
        return toBccpManifestId;
    }

    public void setToBccpManifestId(String toBccpManifestId) {
        this.toBccpManifestId = toBccpManifestId;
    }

    public boolean isConflict() {
        return conflict;
    }

    public void setConflict(boolean conflict) {
        this.conflict = conflict;
    }

    public String getPrevBccManifestId() {
        return prevBccManifestId;
    }

    public void setPrevBccManifestId(String prevBccManifestId) {
        this.prevBccManifestId = prevBccManifestId;
    }

    public String getNextBccManifestId() {
        return nextBccManifestId;
    }

    public void setNextBccManifestId(String nextBccManifestId) {
        this.nextBccManifestId = nextBccManifestId;
    }

    @Override
    public boolean isManifest() {
        return true;
    }

    @Override
    public boolean isAscc() {
        return false;
    }

    @Override
    public boolean isBcc() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BccManifest that = (BccManifest) o;
        return isManifest() == that.isManifest() &&
                isAscc() == that.isAscc() &&
                isBcc() == that.isBcc() &&
                Objects.equals(bccManifestId, that.bccManifestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bccManifestId, isManifest(), isAscc(), isBcc());
    }
}
