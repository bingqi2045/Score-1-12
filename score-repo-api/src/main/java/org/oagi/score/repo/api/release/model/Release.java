package org.oagi.score.repo.api.release.model;

import org.oagi.score.repo.api.base.Auditable;

import java.io.Serializable;

public class Release extends Auditable implements Comparable<Release>, Serializable {

    private String releaseId;

    private String guid;

    private String releaseNum;

    private String releaseNote;

    private String releaseLicense;

    private ReleaseState state;

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getReleaseNum() {
        return releaseNum;
    }

    public void setReleaseNum(String releaseNum) {
        this.releaseNum = releaseNum;
    }

    public String getReleaseNote() {
        return releaseNote;
    }

    public void setReleaseNote(String releaseNote) {
        this.releaseNote = releaseNote;
    }

    public String getReleaseLicense() {
        return releaseLicense;
    }

    public void setReleaseLicense(String releaseLicense) {
        this.releaseLicense = releaseLicense;
    }

    public ReleaseState getState() {
        return state;
    }

    public void setState(ReleaseState state) {
        this.state = state;
    }

    @Override
    public int compareTo(Release o) {
        return this.getLastUpdateTimestamp().compareTo(o.getLastUpdateTimestamp());
    }
}
