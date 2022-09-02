package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;
import java.math.BigInteger;

public class AccManifest implements CcManifest, Serializable {

    private BigInteger accManifestId;

    private String releaseId;

    private String accId;

    private BigInteger basedAccManifestId;

    private boolean conflict;

    private String logId;

    private BigInteger prevAccManifestId;

    private BigInteger nextAccManifestId;

    public BigInteger getAccManifestId() {
        return accManifestId;
    }

    public void setAccManifestId(BigInteger accManifestId) {
        this.accManifestId = accManifestId;
    }

    @Override
    public BigInteger getManifestId() {
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

    public BigInteger getBasedAccManifestId() {
        return basedAccManifestId;
    }

    public void setBasedAccManifestId(BigInteger basedAccManifestId) {
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

    public BigInteger getPrevAccManifestId() {
        return prevAccManifestId;
    }

    public void setPrevAccManifestId(BigInteger prevAccManifestId) {
        this.prevAccManifestId = prevAccManifestId;
    }

    public BigInteger getNextAccManifestId() {
        return nextAccManifestId;
    }

    public void setNextAccManifestId(BigInteger nextAccManifestId) {
        this.nextAccManifestId = nextAccManifestId;
    }
}
