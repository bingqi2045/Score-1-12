package org.oagi.score.repo.api.agency.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;
import org.oagi.score.repo.api.corecomponent.model.CcState;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.io.Serializable;
import java.math.BigInteger;

public class AgencyIdListManifest implements CcManifest, Serializable {

    private String agencyIdListManifestId;

    private String releaseId;

    private String agencyIdListId;

    private String basedAgencyIdListManifestId;

    private String logId;

    private boolean conflict;

    private BigInteger prevAgencyIdListManifestId;

    private BigInteger nextAgencyIdListManifestId;

    @Override
    public String getManifestId() {
        return agencyIdListManifestId;
    }

    @Override
    public String getBasedCcId() {
        return getAgencyIdListId();
    }

    public String getAgencyIdListManifestId() {
        return agencyIdListManifestId;
    }

    public void setAgencyIdListManifestId(String agencyIdListManifestId) {
        this.agencyIdListManifestId = agencyIdListManifestId;
    }

    @Override
    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getAgencyIdListId() {
        return agencyIdListId;
    }

    public void setAgencyIdListId(String agencyIdListId) {
        this.agencyIdListId = agencyIdListId;
    }

    public String getBasedAgencyIdListManifestId() {
        return basedAgencyIdListManifestId;
    }

    public void setBasedAgencyIdListManifestId(String basedAgencyIdListManifestId) {
        this.basedAgencyIdListManifestId = basedAgencyIdListManifestId;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public boolean isConflict() {
        return conflict;
    }

    public void setConflict(boolean conflict) {
        this.conflict = conflict;
    }

    public BigInteger getPrevAgencyIdListManifestId() {
        return prevAgencyIdListManifestId;
    }

    public void setPrevAgencyIdListManifestId(BigInteger prevAgencyIdListManifestId) {
        this.prevAgencyIdListManifestId = prevAgencyIdListManifestId;
    }

    public BigInteger getNextAgencyIdListManifestId() {
        return nextAgencyIdListManifestId;
    }

    public void setNextAgencyIdListManifestId(BigInteger nextAgencyIdListManifestId) {
        this.nextAgencyIdListManifestId = nextAgencyIdListManifestId;
    }
}
