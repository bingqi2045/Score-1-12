package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;

public class BccpManifest implements CcManifest, Serializable {

    private String bccpManifestId;

    private String releaseId;

    private String bccpId;

    private String bdtManifestId;

    private boolean conflict;

    private String logId;

    private String prevBccpManifestId;

    private String nextBccpManifestId;

    public String getBccpManifestId() {
        return bccpManifestId;
    }

    public void setBccpManifestId(String bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
    }

    @Override
    public String getManifestId() {
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

    public String getBdtManifestId() {
        return bdtManifestId;
    }

    public void setBdtManifestId(String bdtManifestId) {
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

    public String getPrevBccpManifestId() {
        return prevBccpManifestId;
    }

    public void setPrevBccpManifestId(String prevBccpManifestId) {
        this.prevBccpManifestId = prevBccpManifestId;
    }

    public String getNextBccpManifestId() {
        return nextBccpManifestId;
    }

    public void setNextBccpManifestId(String nextBccpManifestId) {
        this.nextBccpManifestId = nextBccpManifestId;
    }
}
