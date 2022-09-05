package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;
import java.math.BigInteger;

public class DtManifest implements CcManifest, Serializable {

    private String dtManifestId;

    private String releaseId;

    private String dtId;

    private String basedDtManifestId;

    private boolean conflict;

    private String logId;

    private String prevDtManifestId;

    private String nextDtManifestId;

    public String getDtManifestId() {
        return dtManifestId;
    }

    public void setDtManifestId(String dtManifestId) {
        this.dtManifestId = dtManifestId;
    }

    @Override
    public String getManifestId() {
        return getDtManifestId();
    }

    @Override
    public String getReleaseId() {
        return releaseId;
    }

    @Override
    public String getBasedCcId() {
        return getDtId();
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getDtId() {
        return dtId;
    }

    public void setDtId(String dtId) {
        this.dtId = dtId;
    }

    public String getBasedDtManifestId() {
        return basedDtManifestId;
    }

    public void setBasedDtManifestId(String basedDtManifestId) {
        this.basedDtManifestId = basedDtManifestId;
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

    public String getPrevDtManifestId() {
        return prevDtManifestId;
    }

    public void setPrevDtManifestId(String prevDtManifestId) {
        this.prevDtManifestId = prevDtManifestId;
    }

    public String getNextDtManifestId() {
        return nextDtManifestId;
    }

    public void setNextDtManifestId(String nextDtManifestId) {
        this.nextDtManifestId = nextDtManifestId;
    }
}
