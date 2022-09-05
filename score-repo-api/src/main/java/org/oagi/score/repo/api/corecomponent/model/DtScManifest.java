package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;
import java.math.BigInteger;

public class DtScManifest implements CcManifest, Serializable {

    private String dtScManifestId;

    private String releaseId;

    private String dtScId;

    private String ownerDtManifestId;

    private boolean conflict;

    private String logId;

    private String prevDtScManifestId;

    private String nextDtScManifestId;

    public String getDtScManifestId() {
        return dtScManifestId;
    }

    public void setDtScManifestId(String dtScManifestId) {
        this.dtScManifestId = dtScManifestId;
    }

    @Override
    public String getManifestId() {
        return getDtScManifestId();
    }

    @Override
    public String getReleaseId() {
        return releaseId;
    }

    @Override
    public String getBasedCcId() {
        return getDtScId();
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getDtScId() {
        return dtScId;
    }

    public void setDtScId(String dtScId) {
        this.dtScId = dtScId;
    }

    public String getOwnerDtManifestId() {
        return ownerDtManifestId;
    }

    public void setOwnerDtManifestId(String ownerDtManifestId) {
        this.ownerDtManifestId = ownerDtManifestId;
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

    public String getPrevDtScManifestId() {
        return prevDtScManifestId;
    }

    public void setPrevDtScManifestId(String prevDtScManifestId) {
        this.prevDtScManifestId = prevDtScManifestId;
    }

    public String getNextDtScManifestId() {
        return nextDtScManifestId;
    }

    public void setNextDtScManifestId(String nextDtScManifestId) {
        this.nextDtScManifestId = nextDtScManifestId;
    }
}
