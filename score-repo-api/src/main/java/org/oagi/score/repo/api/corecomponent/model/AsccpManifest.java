package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;
import java.math.BigInteger;

public class AsccpManifest implements CcManifest, Serializable {

    private String asccpManifestId;

    private String releaseId;

    private String asccpId;

    private String roleOfAccManifestId;

    private boolean conflict;

    private String logId;

    private String prevAsccpManifestId;

    private String nextAsccpManifestId;

    public String getAsccpManifestId() {
        return asccpManifestId;
    }

    public void setAsccpManifestId(String asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
    }

    @Override
    public String getManifestId() {
        return getAsccpManifestId();
    }

    @Override
    public String getReleaseId() {
        return releaseId;
    }

    @Override
    public String getBasedCcId() {
        return getAsccpId();
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getAsccpId() {
        return asccpId;
    }

    public void setAsccpId(String asccpId) {
        this.asccpId = asccpId;
    }

    public String getRoleOfAccManifestId() {
        return roleOfAccManifestId;
    }

    public void setRoleOfAccManifestId(String roleOfAccManifestId) {
        this.roleOfAccManifestId = roleOfAccManifestId;
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

    public String getPrevAsccpManifestId() {
        return prevAsccpManifestId;
    }

    public void setPrevAsccpManifestId(String prevAsccpManifestId) {
        this.prevAsccpManifestId = prevAsccpManifestId;
    }

    public String getNextAsccpManifestId() {
        return nextAsccpManifestId;
    }

    public void setNextAsccpManifestId(String nextAsccpManifestId) {
        this.nextAsccpManifestId = nextAsccpManifestId;
    }
}
