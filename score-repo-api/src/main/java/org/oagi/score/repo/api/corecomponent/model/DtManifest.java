package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;
import java.math.BigInteger;

public class DtManifest implements CcManifest, Serializable {

    private BigInteger dtManifestId;

    private String releaseId;

    private BigInteger dtId;

    private BigInteger basedDtManifestId;

    private boolean conflict;

    private String logId;

    private BigInteger prevDtManifestId;

    private BigInteger nextDtManifestId;

    public BigInteger getDtManifestId() {
        return dtManifestId;
    }

    public void setDtManifestId(BigInteger dtManifestId) {
        this.dtManifestId = dtManifestId;
    }

    @Override
    public BigInteger getManifestId() {
        return getDtManifestId();
    }

    @Override
    public String getReleaseId() {
        return releaseId;
    }

    @Override
    public BigInteger getBasedCcId() {
        return getDtId();
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public BigInteger getDtId() {
        return dtId;
    }

    public void setDtId(BigInteger dtId) {
        this.dtId = dtId;
    }

    public BigInteger getBasedDtManifestId() {
        return basedDtManifestId;
    }

    public void setBasedDtManifestId(BigInteger basedDtManifestId) {
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

    public BigInteger getPrevDtManifestId() {
        return prevDtManifestId;
    }

    public void setPrevDtManifestId(BigInteger prevDtManifestId) {
        this.prevDtManifestId = prevDtManifestId;
    }

    public BigInteger getNextDtManifestId() {
        return nextDtManifestId;
    }

    public void setNextDtManifestId(BigInteger nextDtManifestId) {
        this.nextDtManifestId = nextDtManifestId;
    }
}
