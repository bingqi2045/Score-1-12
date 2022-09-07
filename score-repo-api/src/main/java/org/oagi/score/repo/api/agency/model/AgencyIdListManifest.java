package org.oagi.score.repo.api.agency.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;
import java.math.BigInteger;

public class AgencyIdListManifest implements CcManifest, Serializable {

    private String agencyIdListManifestId;

    private String releaseId;

    private String agencyIdListId;

    private String basedAgencyIdListManifestId;

    private String logId;

    private boolean conflict;

    private String prevAgencyIdListManifestId;

    private String nextAgencyIdListManifestId;

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

    public String getPrevAgencyIdListManifestId() {
        return prevAgencyIdListManifestId;
    }

    public void setPrevAgencyIdListManifestId(String prevAgencyIdListManifestId) {
        this.prevAgencyIdListManifestId = prevAgencyIdListManifestId;
    }

    public String getNextAgencyIdListManifestId() {
        return nextAgencyIdListManifestId;
    }

    public void setNextAgencyIdListManifestId(String nextAgencyIdListManifestId) {
        this.nextAgencyIdListManifestId = nextAgencyIdListManifestId;
    }
}
