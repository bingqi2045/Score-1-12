package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;
import java.math.BigInteger;

public class BccpManifest implements CcManifest, Serializable {

    private BigInteger bccpManifestId;

    private String releaseId;

    private String bccpId;

    private BigInteger bdtManifestId;

    private boolean conflict;

    private String logId;

    private BigInteger prevBccpManifestId;

    private BigInteger nextBccpManifestId;

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }

    public void setBccpManifestId(BigInteger bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    @Override
    public BigInteger getManifestId() {
        return getBccpManifestId();
    }

    @Override
    public String getReleaseId() {
        return releaseId;
    }

    @Override
    public String getBasedCcId() {
        return getBccpId();
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getBccpId() {
        return bccpId;
    }

    public void setBccpId(String bccpId) {
        this.bccpId = bccpId;
    }

    public BigInteger getBdtManifestId() {
        return bdtManifestId;
    }

    public void setBdtManifestId(BigInteger bdtManifestId) {
        this.bdtManifestId = bdtManifestId;
    }

    public boolean isConflict() {
        return conflict;
    }

    public void setConflict(boolean conflict) {
        this.conflict = conflict;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public BigInteger getPrevBccpManifestId() {
        return prevBccpManifestId;
    }

    public void setPrevBccpManifestId(BigInteger prevBccpManifestId) {
        this.prevBccpManifestId = prevBccpManifestId;
    }

    public BigInteger getNextBccpManifestId() {
        return nextBccpManifestId;
    }

    public void setNextBccpManifestId(BigInteger nextBccpManifestId) {
        this.nextBccpManifestId = nextBccpManifestId;
    }
}
