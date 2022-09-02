package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;
import java.math.BigInteger;

public class DtScManifest implements CcManifest, Serializable {

    private BigInteger dtScManifestId;

    private String releaseId;

    private String dtScId;

    private BigInteger ownerDtManifestId;

    private boolean conflict;

    private String logId;

    private BigInteger prevDtScManifestId;

    private BigInteger nextDtScManifestId;

    public BigInteger getDtScManifestId() {
        return dtScManifestId;
    }

    public void setDtScManifestId(BigInteger dtScManifestId) {
        this.dtScManifestId = dtScManifestId;
    }

    @Override
    public BigInteger getManifestId() {
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

    public BigInteger getOwnerDtManifestId() {
        return ownerDtManifestId;
    }

    public void setOwnerDtManifestId(BigInteger ownerDtManifestId) {
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

    public BigInteger getPrevDtScManifestId() {
        return prevDtScManifestId;
    }

    public void setPrevDtScManifestId(BigInteger prevDtScManifestId) {
        this.prevDtScManifestId = prevDtScManifestId;
    }

    public BigInteger getNextDtScManifestId() {
        return nextDtScManifestId;
    }

    public void setNextDtScManifestId(BigInteger nextDtScManifestId) {
        this.nextDtScManifestId = nextDtScManifestId;
    }
}
