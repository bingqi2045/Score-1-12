package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;
import java.util.Objects;

public class AsccManifest implements CcManifest, CcAssociation, Serializable {

    private String asccManifestId;

    private String releaseId;

    private String asccId;

    private String seqKeyId;

    private String fromAccManifestId;

    private String toAsccpManifestId;

    private boolean conflict;

    private String prevAsccManifestId;

    private String nextAsccManifestId;

    public String getAsccManifestId() {
        return asccManifestId;
    }

    public void setAsccManifestId(String asccManifestId) {
        this.asccManifestId = asccManifestId;
    }

    @Override
    public String getManifestId() {
        return getAsccManifestId();
    }

    @Override
    public String getReleaseId() {
        return releaseId;
    }

    @Override
    public String getBasedCcId() {
        return getAsccId();
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getAsccId() {
        return asccId;
    }

    public void setAsccId(String asccId) {
        this.asccId = asccId;
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

    public String getToAsccpManifestId() {
        return toAsccpManifestId;
    }

    public void setToAsccpManifestId(String toAsccpManifestId) {
        this.toAsccpManifestId = toAsccpManifestId;
    }

    public boolean isConflict() {
        return conflict;
    }

    public void setConflict(boolean conflict) {
        this.conflict = conflict;
    }

    public String getPrevAsccManifestId() {
        return prevAsccManifestId;
    }

    public void setPrevAsccManifestId(String prevAsccManifestId) {
        this.prevAsccManifestId = prevAsccManifestId;
    }

    public String getNextAsccManifestId() {
        return nextAsccManifestId;
    }

    public void setNextAsccManifestId(String nextAsccManifestId) {
        this.nextAsccManifestId = nextAsccManifestId;
    }

    @Override
    public boolean isManifest() {
        return true;
    }

    @Override
    public boolean isAscc() {
        return true;
    }

    @Override
    public boolean isBcc() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AsccManifest that = (AsccManifest) o;
        return isManifest() == that.isManifest() &&
                isAscc() == that.isAscc() &&
                isBcc() == that.isBcc() &&
                Objects.equals(asccManifestId, that.asccManifestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asccManifestId, isManifest(), isAscc(), isBcc());
    }
}
