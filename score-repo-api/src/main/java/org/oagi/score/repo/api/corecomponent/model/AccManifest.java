package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;

public class AccManifest implements CcManifest, Serializable {

    private String accManifestId;

    private String releaseId;

    private String accId;

    private String basedAccManifestId;

    private boolean conflict;

    private String logId;

    private String prevAccManifestId;

    private String nextAccManifestId;

    public String getAccManifestId() {
        return accManifestId;
    }

    public void setAccManifestId(String accManifestId) {
        this.accManifestId = accManifestId;
    }

    @Override
    public String getManifestId() {
        return getAccManifestId();
    }

    @Override
    public String getReleaseId() {
        return releaseId;
    }

    @Override
    public String getBasedCcId() {
        return getAccId();
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public String getBasedAccManifestId() {
        return basedAccManifestId;
    }

    public void setBasedAccManifestId(String basedAccManifestId) {
        this.basedAccManifestId = basedAccManifestId;
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

    public String getPrevAccManifestId() {
        return prevAccManifestId;
    }

    public void setPrevAccManifestId(String prevAccManifestId) {
        this.prevAccManifestId = prevAccManifestId;
    }

    public String getNextAccManifestId() {
        return nextAccManifestId;
    }

    public void setNextAccManifestId(String nextAccManifestId) {
        this.nextAccManifestId = nextAccManifestId;
    }
}
