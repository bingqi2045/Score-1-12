package org.oagi.score.repo.api.bie.model;

import org.oagi.score.repo.api.base.Auditable;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.io.Serializable;

public class TopLevelAsbiep extends Auditable implements Serializable {

    private String topLevelAsbiepId;

    private ScoreUser owner;

    private String asbiepId;

    private String releaseId;

    private String propertyTerm;

    private String guid;

    private BieState state;

    private String status;

    private String version;

    public String getTopLevelAsbiepId() {
        return topLevelAsbiepId;
    }

    public void setTopLevelAsbiepId(String topLevelAsbiepId) {
        this.topLevelAsbiepId = topLevelAsbiepId;
    }

    public ScoreUser getOwner() {
        return owner;
    }

    public void setOwner(ScoreUser owner) {
        this.owner = owner;
    }

    public String getAsbiepId() {
        return asbiepId;
    }

    public void setAsbiepId(String asbiepId) {
        this.asbiepId = asbiepId;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getPropertyTerm() {
        return propertyTerm;
    }

    public void setPropertyTerm(String propertyTerm) {
        this.propertyTerm = propertyTerm;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public BieState getState() {
        return state;
    }

    public void setState(BieState state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
