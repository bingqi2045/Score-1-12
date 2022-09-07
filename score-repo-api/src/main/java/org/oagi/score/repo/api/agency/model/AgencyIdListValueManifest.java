package org.oagi.score.repo.api.agency.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;
import java.math.BigInteger;

public class AgencyIdListValueManifest implements CcManifest, Serializable {

    private String agencyIdListValueManifestId;

    private String releaseId;

    private String agencyIdListValueId;

    private String agencyIdListManifestId;

    private boolean conflict;

    private String prevAgencyIdListManifestId;

    private String nextAgencyIdListManifestId;

    @Override
    public String getManifestId() {
        return agencyIdListValueManifestId;
    }

    @Override
    public String getBasedCcId() {
        return getAgencyIdListValueId();
    }

    public String getAgencyIdListValueManifestId() {
        return agencyIdListValueManifestId;
    }

    public void setAgencyIdListValueManifestId(String agencyIdListValueManifestId) {
        this.agencyIdListValueManifestId = agencyIdListValueManifestId;
    }

    @Override
    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getAgencyIdListValueId() {
        return agencyIdListValueId;
    }

    public void setAgencyIdListValueId(String agencyIdListValueId) {
        this.agencyIdListValueId = agencyIdListValueId;
    }

    public String getAgencyIdListManifestId() {
        return agencyIdListManifestId;
    }

    public void setAgencyIdListManifestId(String agencyIdListManifestId) {
        this.agencyIdListManifestId = agencyIdListManifestId;
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
